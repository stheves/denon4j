package de.theves.denon4j;

import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;

import java.util.List;

/**
 * Created by Elena on 04.06.2017.
 */
public interface CommandStack {
    Command execute(CommandId commandId, String value) throws CommandNotFoundException;

    List<Command> history();

    void redo();

    boolean canRedo();
}
