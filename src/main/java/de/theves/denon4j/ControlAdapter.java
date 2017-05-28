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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.theves.denon4j.Value.NULL;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class ControlAdapter implements Control {
    private final String prefix;
    private final CommandRegistry registry;

    private Value state;
    private List<Command> commands;


    public ControlAdapter(CommandRegistry registry, String prefix) {
        this.prefix = Objects.requireNonNull(prefix);
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public String getCommandPrefix() {
        return prefix;
    }

    @Override
    public void handle(Event event) {
        state = new Value(event.getParameter().getName());
    }

    @Override
    public void init() {
        // let subclasses override
    }

    @Override
    public void dispose() {
        commands.forEach(command -> registry.deregisterCommand(command.getId()));
    }

    protected CommandRegistry getRegistry() {
        return registry;
    }

    protected Value getState() {
        lazyInitState();
        return state;
    }

    private void lazyInitState() {
        if (null == state && getRequestId() != null) {
            Optional<Event> event = executeCommand(getRequestId(), NULL);
            if (event.isPresent()) {
                state = new Value(event.get().getParameter().getName());
            }
        }
    }

    protected CommandId getRequestId() {
        return null;
    }

    protected List<Command> register(String... parameters) {
        commands = getRegistry().teach(getCommandPrefix(), parameters);
        return commands;
    }

    protected Optional<Event> executeCommand(CommandId downId, Value value) {
        return getRegistry().getCommandStack().execute(downId, value);
    }
}
