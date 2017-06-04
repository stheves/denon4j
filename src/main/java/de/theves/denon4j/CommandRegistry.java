package de.theves.denon4j;

import de.theves.denon4j.internal.CommandStackImpl;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by Elena on 04.06.2017.
 */
public interface CommandRegistry {
    void deregisterCommand(CommandId id);

    Collection<Command> getCommands();

    void printCommands(PrintStream out);

    Command getCommand(CommandId id);

    boolean isRegistered(CommandId id);

    Command register(String prefix, String param);

    List<Command> teach(String prefix, String... parameters);

    CommandStackImpl getCommandStack();
}
