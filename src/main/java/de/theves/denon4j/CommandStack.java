/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.theves.denon4j;

import de.theves.denon4j.internal.net.SetCommand;
import de.theves.denon4j.net.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class CommandStack {
    private final Logger logger = LoggerFactory.getLogger(CommandStack.class);

    private final LinkedList<Command> commandList;

    private final CommandRegistry commandRegistry;

    public CommandStack(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
        commandList = new LinkedList<>();
    }

    public Command execute(CommandId commandId, String value) {
        synchronized (commandList) {
            Command cmd = assertGetCommand(commandId);
            if (cmd instanceof SetCommand) {
                ((SetCommand) cmd).set(value);
            }
            cmd.execute();
            commandList.add(cmd);
            logger.debug("Command executed: " + cmd);
            return cmd;
        }
    }

    public List<Command> history() {
        synchronized (commandList) {
            return Collections.unmodifiableList(commandList);
        }
    }

    public void redo() {
        synchronized (commandList) {
            commandList.getLast().execute();
        }
    }

    public boolean canRedo() {
        synchronized (commandList) {
            return commandList.getLast() != null;
        }
    }

    private Command assertGetCommand(CommandId commandId) {
        if (!commandRegistry.isRegistered(commandId)) {
            throw new CommandNotFoundException("Command not found: " + commandId);
        }
        return commandRegistry.getCommand(commandId);
    }
}
