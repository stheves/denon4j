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

import de.theves.denon4j.model.Command;
import de.theves.denon4j.model.CommandId;
import de.theves.denon4j.model.Parameter;

import java.io.PrintStream;
import java.util.*;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class CommandRegistry {
    private final Map<CommandId, Command> commands;

    public CommandRegistry() {
        this.commands = new HashMap<>();
    }

    public void deregisterCommand(CommandId id) {
        commands.remove(id);
    }

    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public void printCommands(PrintStream out) {
        for (Command cmd : commands.values()) {
            out.println(cmd.toString());
        }
    }

    public Command getCommand(CommandId id) {
        return commands.get(id);
    }

    private Command registerCommand(String prefix, String param) {
        Command cmd = CommandFactory.create(prefix, param);
        this.commands.put(cmd.getId(), cmd);
        return cmd;
    }

    public Collection<Command> registerCommands(String prefix, String... parameters) {
        Collection<Command> result = new ArrayList<>(parameters.length + 1);
        if (parameters.length == 0) {
            result.add(registerCommand(prefix, Parameter.EMPTY.getName()));
        } else {
            for (String param : parameters) {
                result.add(registerCommand(prefix, param));
            }
        }
        return result;
    }
}
