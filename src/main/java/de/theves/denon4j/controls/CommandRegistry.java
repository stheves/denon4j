package de.theves.denon4j.controls;

import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

/**
 * Registry for all known commands. Commands can be registered/deregisted and executed via the {@link CommandStack}.
 */
public interface CommandRegistry {
    /**
     * Deregister the command with the given <code>id</code>.
     *
     * @param id the <code>id</code> of the command to deregister.
     */
    void deregisterCommand(CommandId id);

    /**
     * Returns an unmodifiable collection of all commands.
     *
     * @return an unmodifiable collection of all commands.
     */
    List<Command> getCommands();

    /**
     * Prints the help of all registered commands to the given PrintStream.
     *
     * @param out the stream to write to.
     */
    void printCommands(PrintStream out);

    /**
     * Returns the command for the given <code>id</code>.
     *
     * @param id the <code>id</code> of the command.
     * @return the command to the given <code>id</code>.
     * @throws CommandNotFoundException if no command could be found.
     */
    Command getCommand(CommandId id) throws CommandNotFoundException;

    /**
     * Returns <code>true</code> if the <code>id</code> is registered.
     *
     * @param id the <code>id</code> to check.
     * @return <code>true</code> if registered and <code>false</code> otherwise.
     */
    boolean isRegistered(CommandId id);

    /**
     * Registers a new command with the given <code>prefix</code> and <code>param</code>.
     *
     * @param prefix the command prefix e.g. 'SV'
     * @param param  the command parameter.
     * @return the registered command.
     */
    Command register(String prefix, String param);

    /**
     * Teaches this registry a command and a set of parameters at once.
     *
     * @param prefix     the command prefix.
     * @param parameters the command`s parameters.
     * @return a list containing the registered commands in the order of the parameters.
     */
    List<Command> registerAll(String prefix, String... parameters);

    /**
     * Returns the command stack for this registry.
     *
     * @return the command stack.
     */
    CommandStack getCommandStack();

    /**
     * Find a command by it`s signature.
     *
     * @param signature the signature of the command.
     * @return the command found by the signature or an empty optional.
     */
    Optional<Command> findBySignature(Signature signature);

    /**
     * Finds all commands with the given <code>prefix</code>.
     *
     * @param prefix the command`s prefix.
     * @return all commands with <code>prefix</code>.
     */
    List<Command> findByPrefix(String prefix);
}
