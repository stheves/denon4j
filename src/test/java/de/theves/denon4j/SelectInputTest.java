package de.theves.denon4j;

import de.theves.denon4j.internal.CommandRegistryImpl;
import de.theves.denon4j.internal.net.EventImpl;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.Protocol;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SelectInput}.
 */
public class SelectInputTest {
    @Test
    public void testSelectInput() {
        Protocol protocol = mock(Protocol.class);
        CommandRegistry registry = new CommandRegistryImpl(protocol);
        SelectInput si = new SelectInput(registry);

        si.init();

        List<Command> commands = si.getCommands();
        assertThat(commands.size()).isEqualTo(23);
        si.select(InputSource.CD);

        when(protocol.receive(si.getRequestCommand())).thenReturn(EventImpl.create("SICD"));
        Optional<InputSource> source = si.getSource();
        assertThat(source).hasValue(InputSource.CD);

        verify(protocol).send(registry.getCommands().get(0));
    }
}
