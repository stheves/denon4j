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

package de.theves.denon4j.internal;

import de.theves.denon4j.CommandNotFoundException;
import de.theves.denon4j.CommandRegistry;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.Protocol;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class CommandRegistryImpl implements CommandRegistry {
    private final LinkedHashMap<CommandId, Command> commands;
    private final CommandStackImpl commandStackImpl;
    private final Protocol protocol;

    public CommandRegistryImpl(Protocol protocol) {
        this.commands = new LinkedHashMap<>();
        this.commandStackImpl = new CommandStackImpl(this);
        this.protocol = protocol;
    }

    @Override
    public void deregisterCommand(CommandId id) {
        commands.remove(id);
    }

    @Override
    public List<Command> getCommands() {
        return Collections.unmodifiableList(new ArrayList<>(commands.values()));
    }

    @Override
    public void printCommands(PrintStream out) {
        for (Command cmd : commands.values()) {
            out.println(cmd.toString());
        }
    }

    @Override
    public Command getCommand(CommandId id) {
        if (!isRegistered(id)) {
            throw new CommandNotFoundException("No command for ID '" + id + "' found");
        }
        return commands.get(id);
    }

    @Override
    public boolean isRegistered(CommandId id) {
        return commands.containsKey(id);
    }

    @Override
    public Command register(String prefix, String param) {
        Command cmd = CommandFactory.create(protocol, prefix, param);
        this.commands.put(cmd.getId(), cmd);
        return cmd;
    }

    @Override
    public List<Command> teach(String prefix, String... parameters) {
        List<Command> result = new ArrayList<>(parameters.length + 1);
        if (parameters.length == 0) {
            result.add(register(prefix, Parameter.EMPTY.getValue()));
        } else {
            for (String param : parameters) {
                result.add(register(prefix, param));
            }
        }
        return result;
    }

    @Override
    public CommandStackImpl getCommandStack() {
        return commandStackImpl;
    }
}
