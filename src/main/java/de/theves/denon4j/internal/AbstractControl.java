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

import de.theves.denon4j.CommandRegistry;
import de.theves.denon4j.Control;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Class description.
 *
 * @author Sascha Theves
 */
public abstract class AbstractControl implements Control {
    private static final Parameter DIRTY = Parameter.create("");

    private final String prefix;
    private final CommandRegistry registry;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Object stateMonitor = new Object();

    private Parameter state = DIRTY;
    private final List<Command> commands = new ArrayList<>(10);

    public AbstractControl(CommandRegistry registry, String prefix) {
        this.prefix = Objects.requireNonNull(prefix);
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void init() {
        if (initialized.compareAndSet(false, true)) {
            doInit();
        } else {
            throw new AlreadyInitException("This control has already been initialized");
        }
    }

    @Override
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    protected abstract void doInit();

    @Override
    public String getCommandPrefix() {
        return prefix;
    }

    @Override
    public void handle(Event event) {
        checkInitialized();
        state = event.getParameter();
    }

    private void checkInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("Not initialized");
        }
    }

    @Override
    public void dispose() {
        if (initialized.compareAndSet(true, false)) {
            commands.forEach(command -> registry.deregisterCommand(command.getId()));
        }
    }

    protected abstract RequestCommand getRequestCommand();

    protected CommandRegistry getRegistry() {
        return registry;
    }

    protected Parameter getState() {
        checkInitialized();
        synchronized (stateMonitor) {
            initState();
            return state;
        }
    }

    private void initState() {
        if (DIRTY == state) {
            executeCommand(getRequestCommand().getId());
            state = getRequestCommand().getReceived().getParameter();
        }
    }

    protected void executeCommand(CommandId commandId) {
        executeCommand(commandId, null);
    }

    protected List<Command> register(String... parameters) {
        commands.addAll(getRegistry().registerAll(getCommandPrefix(), parameters));
        return commands;
    }

    protected void executeCommand(CommandId downId, String value) {
        Command execute = getRegistry().getCommandStack().execute(downId, value);
        if(execute instanceof RequestCommand) {
           return;
        }
        state = DIRTY;
    }
}
