package de.theves.denon4j.internal;

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.controls.*;
import de.theves.denon4j.internal.net.EventImpl;
import de.theves.denon4j.net.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        assertThat(commands.size()).isEqualTo(23);
        assertThat(registry.findByPrefix("SI")).hasSize(23);

        // execute control
        si.select(InputSource.SAT_CBL);

        when(protocol.receive((RequestCommand) cmd("SI?"))).thenReturn(event("SISAT/CBL"));
        Optional<InputSource> source = si.getSource();
        assertThat(source).hasValue(InputSource.SAT_CBL);

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
        assertThat(mute.getCommands()).hasSize(3).containsExactlyInAnyOrder(commands("MUON", "MUOFF", "MU?"));
    }

    @Test(expected = CommandNotFoundException.class)
    public void testUnknownCommand() {
        registry.getCommand(CommandId.random());
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
    }

    @Test
    public void testMainZoneToggle() {
        Toggle toggle = avr1912.mainZone();
        assertThat(toggle.getCommands()).hasSize(3).containsExactlyInAnyOrder(commands("ZMON", "ZMOFF", "ZM?"));
    }

    @Test
    public void testNetworkControl() {
        Select<NetControl> selectNetworkControl = avr1912.selectNetworkControl();
        assertThat(selectNetworkControl.getCommands()).hasSize(20);
        assertThat(selectNetworkControl.getCommandPrefix()).isEqualTo("NS");

        selectNetworkControl.select(NetControl.CURSOR_DOWN);
        verify(protocol).send(cmd("NS91"));
    }

    @Test
    public void testHealth() {
        // check all controls initialized
        assertThat(avr1912.getControls()
                .stream()
                .filter(c -> !c.isInitialized())
                .count()).isEqualTo(0);

        // and added to dispatcher
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
