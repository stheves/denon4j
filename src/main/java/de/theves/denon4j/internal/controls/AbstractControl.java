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

package de.theves.denon4j.internal.controls;

import de.theves.denon4j.controls.CommandRegistry;
import de.theves.denon4j.controls.Control;
import de.theves.denon4j.controls.NotYetInitializedException;
import de.theves.denon4j.internal.net.AlreadyInitException;
import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Class description.
 *
 * @author stheves
 */
public abstract class AbstractControl implements Control {
    private static final Parameter DIRTY = ParameterImpl.createParameter("DIRTY");

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String prefix;
    private final CommandRegistry registry;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final List<Command> commands = new ArrayList<>(10);

    private final Object stateMonitor = new Object();
    private Parameter state = DIRTY;
    private String name;

    AbstractControl(CommandRegistry registry, String prefix) {
        this.prefix = Objects.requireNonNull(prefix);
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void handle(Event event) {
        checkInitialized();
        state = event.getParameter();
        logger.debug("Handled event: {}", event);
    }

    @Override
    public String getCommandPrefix() {
        return prefix;
    }

    @Override
    public void init() {
        if (initialized.compareAndSet(false, true)) {
            doInit();
        } else {
            throw new AlreadyInitException("This control has already been initialized");
        }
        logger.debug("Control initialized: {}", this);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public void dispose() {
        if (initialized.compareAndSet(true, false)) {
            commands.forEach(command -> registry.deregisterCommand(command.getId()));
        }
        // ignore if we were not yet initialized
        logger.debug("Control disposed: {}", this);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean supports(Event event) {
        return getCommandPrefix().equals(event.getPrefix());
    }

    protected abstract void doInit();

    private void checkInitialized() {
        if (!initialized.get()) {
            throw new NotYetInitializedException(this);
        }
    }

    Parameter getState() {
        checkInitialized();
        synchronized (stateMonitor) {
            initState();
            return state;
        }
    }

    private void initState() {
        int retries = 0;
        while (state == DIRTY && retries < 3) {
            executeCommand(getRequestCommand().getId());
            Event event = getRequestCommand().getReceived();
            if (supports(event)) {
                state = event.getParameter();
            }
            retries++;
        }
        if (state == DIRTY) {
            throw new IllegalStateException("Could not get result for request: " + getRequestCommand());
        }
    }

    void executeCommand(CommandId commandId) {
        executeCommand(commandId, null);
    }

    protected abstract RequestCommand getRequestCommand();

    void executeCommand(CommandId downId, String value) {
        Command cmd = getRegistry().getCommandStack().execute(downId, value);
        if (cmd.isDirtying()) {
            state = DIRTY;
        }
    }

    CommandRegistry getRegistry() {
        return registry;
    }

    List<Command> register(String... parameters) {
        commands.addAll(getRegistry().registerAll(getCommandPrefix(), parameters));
        return commands;
    }

    @Override
    public String toString() {
        return "Control{" +
                "prefix='" + prefix + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
