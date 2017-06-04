package de.theves.denon4j;

import de.theves.denon4j.internal.net.EventImpl;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.*;
import org.junit.Before;
import org.junit.Test;

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

        when(protocol.receive(si.getRequestCommand())).thenReturn(event("SISAT/CBL"));
        Optional<InputSource> source = si.getSource();
        assertThat(source).hasValue(InputSource.SAT_CBL);

        verify(protocol).send(sig("SISAT/CBL"));
    }

    @Test
    public void testPowerControl() {
        Toggle power = avr1912.power();
        assertThat(power.getCommands())
                .hasSize(3)
                .containsAll(sigs("PWON", "PWSTANDBY", "PW?"));

        when(protocol.receive((RequestCommand) sig("PW?")))
                .thenReturn(event("PWSTANDBY"), event("PWON"));

        assertThat(power.switchedOff()).isTrue();
        power.toggle();
        verify(protocol).send(sig("PWON"));
        assertThat(power.switchedOff()).isFalse();
        assertThat(power.switchedOn()).isTrue();
        power.toggle();
        verify(protocol).send(sig("PWSTANDBY"));
    }

    @Test
    public void testMuteControl() {
        Toggle mute = avr1912.mute();
        assertThat(mute.getCommands()).hasSize(3).containsAll(sigs("MUON", "MUOFF", "MU?"));
    }

    @Test(expected = CommandNotFoundException.class)
    public void testUnknownCommand() {
        registry.getCommand(CommandId.random());
    }

    @Test
    public void testMasterSlider() {
        Slider slider = avr1912.masterVolume();
        assertThat(slider.getCommands()).hasSize(4).containsAll(sigs("MVUP", "MVDOWN", "MV", "MV?"));
        when(protocol.receive((RequestCommand) sig("MV?"))).thenReturn(event("MV45"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.slideUp();
        verify(protocol).send(sig("MVUP"));

        slider.slideDown();
        verify(protocol).send(sig("MVDOWN"));

        slider.set("55");
        verify(protocol).send(sig("MV55"));
    }

    private Event event(String e) {
        return EventImpl.create(e);
    }

    private List<Command> sigs(String... sig) {
        return Stream.of(sig).map(s -> sig(s)).collect(Collectors.toList());
    }

    private Command sig(String s) {
        return registry.findBySignature(() -> s).get();
    }
}
