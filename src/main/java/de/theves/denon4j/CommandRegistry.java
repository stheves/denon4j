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

import de.theves.denon4j.net.Protocol;

import java.io.PrintStream;
import java.util.*;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class CommandRegistry {
    private final Map<CommandId, Command> commands;
    private final CommandStack commandStack;
    private final Protocol protocol;

    public CommandRegistry(Protocol protocol) {
        this.commands = new LinkedHashMap<>();
        this.commandStack = new CommandStack(this);
        this.protocol = protocol;
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

    public Optional<Command> getCommand(CommandId id) {
        return Optional.ofNullable(commands.get(id));
    }

    public Command register(String prefix, String param) {
        Command cmd = CommandFactory.create(protocol, prefix, param);
        this.commands.put(cmd.getId(), cmd);
        return cmd;
    }

    public List<Command> teach(String prefix, String... parameters) {
        List<Command> result = new ArrayList<>(parameters.length + 1);
        if (parameters.length == 0) {
            result.add(register(prefix, Parameter.EMPTY.getName()));
        } else {
            for (String param : parameters) {
                result.add(register(prefix, param));
            }
        }
        return result;
    }

    public CommandStack getCommandStack() {
        return commandStack;
    }
}
