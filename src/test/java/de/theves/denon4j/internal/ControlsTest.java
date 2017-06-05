package de.theves.denon4j.internal;

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.controls.*;
import de.theves.denon4j.internal.net.EventImpl;
import de.theves.denon4j.net.*;
import org.assertj.core.api.AbstractLongAssert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for basic controls.
 */
public class ControlsTest {
    private Avr1912 avr1912;
    private Protocol protocol;
    private CommandRegistry registry;

    @Before
    public void setup() {
        protocol = mock(Protocol.class);
        avr1912 = new Avr1912(protocol);
        registry = avr1912.getRegistry();
    }

    @Test(expected = ConnectException.class)
    public void testConnectionHandling() {
        avr1912.connect(100);
        verify(protocol).establishConnection(100);

        when(protocol.isConnected()).thenReturn(Boolean.TRUE);
        assertThat(avr1912.isConnected()).isTrue();

        avr1912.disconnect();
        verify(protocol).disconnect();

        when(protocol.isConnected()).thenReturn(Boolean.FALSE);
        assertThat(avr1912.isConnected()).isFalse();

        doThrow(new ConnectException("Failure")).when(protocol).establishConnection(137);
        avr1912.connect(137);
    }

    @Test
    public void testSelectControl() {
        Select<InputSource> si = avr1912.selectInput();
        List<Command> commands = si.getCommands();
        assertThat(commands.size()).isEqualTo(InputSource.values().length + 1);
        assertThat(registry.findByPrefix("SI")).hasSize(InputSource.values().length + 1);

        // execute control
        si.select(InputSource.SAT_CBL);

        when(protocol.receive((RequestCommand) cmd("SI?"))).thenReturn(event("SISAT/CBL"));
        InputSource source = si.getSource();
        assertThat(source).isEqualTo(InputSource.SAT_CBL);

        Command cmd = cmd("SISAT/CBL");
        verify(protocol).send(cmd);
        assertThat(cmd.getExecutedAt()).isAfter(LocalDateTime.MIN);
    }

    @Test
    public void testPowerControl() {
        Toggle power = avr1912.power();
        assertThat(power.getCommands())
                .hasSize(3)
                .containsExactlyInAnyOrder(commands("PWON", "PWSTANDBY", "PW?"));

        when(protocol.receive((RequestCommand) cmd("PW?")))
                .thenReturn(event("PWSTANDBY"), event("PWON"));

        assertThat(power.getSwitchState()).isEqualTo(SwitchState.STANDBY);
        power.toggle();
        verify(protocol).send(cmd("PWON"));
        assertThat(power.getSwitchState()).isEqualTo(SwitchState.ON);
        power.toggle();
        verify(protocol).send(cmd("PWSTANDBY"));
    }

    @Test
    public void testMuteControl() {
        Toggle mute = avr1912.mute();
        assertThat(mute.getCommands()).containsExactlyInAnyOrder(commands("MUON", "MUOFF", "MU?"));
    }

    @Test
    public void testUnknownCommand() {
        assertThatThrownBy(() ->
                registry.getCommand(CommandId.random())).isInstanceOf(CommandNotFoundException.class);
    }

    @Test
    public void testMasterSlider() {
        Slider slider = avr1912.masterVolume();
        assertThat(slider.getCommands()).hasSize(4).containsExactlyInAnyOrder(commands("MVUP", "MVDOWN", "MV", "MV?"));
        when(protocol.receive((RequestCommand) cmd("MV?"))).thenReturn(event("MV45"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.slideUp();
        verify(protocol).send(cmd("MVUP"));

        slider.slideDown();
        verify(protocol).send(cmd("MVDOWN"));

        slider.set("55");
        verify(protocol).send(cmd("MV55"));

        // test validity checks
        assertThat(slider.isValid()).isTrue();
        assertThatThrownBy(() -> slider.set("invalid")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set("MV55")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set(" 55")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set("5")).isInstanceOf(InvalidSignatureException.class);
    }

    @Test
    public void testMainZoneToggle() {
        Toggle toggle = avr1912.mainZone();
        assertThat(toggle.getCommands()).hasSize(3).containsExactlyInAnyOrder(commands("ZMON", "ZMOFF", "ZM?"));
    }

    @Test
    public void testNetworkControl() {
        Select<ExtendedSettings> selectNetworkControl = avr1912.selectNetworkControl();
        assertThat(selectNetworkControl.getCommands()).hasSize(ExtendedSettings.values().length);
        assertThat(selectNetworkControl.getCommandPrefix()).isEqualTo("NS");
        assertThat(registry.findBySignature(() -> "NS?")).isEmpty();

        selectNetworkControl.select(ExtendedSettings.CURSOR_DOWN);
        verify(protocol).send(cmd("NS91"));
    }

    @Test
    public void testCorrectInit() {
        // check all controls initialized
        assertControlsInitialized();

        // and added to dispatcher
        assertAddedToDispatcher();
    }

    private AbstractLongAssert<?> assertControlsInitialized() {
        return assertThat(avr1912.getControls()
                .stream()
                .filter(c -> !c.isInitialized())
                .count()).isEqualTo(0);
    }

    private void assertAddedToDispatcher() {
        assertThat(avr1912.getEventDispatcher().getControls())
                .containsExactlyInAnyOrder(
                        avr1912.getControls()
                                .stream()
                                .toArray(value -> new Control[avr1912.getControls().size()])
                );
    }

    private Event event(String e) {
        return EventImpl.create(e);
    }

    private Command[] commands(String... sig) {
        List<Command> commandList = Stream.of(sig).map(this::cmd).collect(Collectors.toList());
        return commandList.toArray(new Command[commandList.size()]);
    }

    private Command cmd(String s) {
        return registry.findBySignature(
                () -> s
        ).orElseThrow(
                () -> new CommandNotFoundException("Command with signature '" + s + "' not found")
        );
    }
}
