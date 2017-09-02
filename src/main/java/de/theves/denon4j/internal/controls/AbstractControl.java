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
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Event;
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
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String prefix;
    protected final CommandRegistry registry;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final List<Command> commands = new ArrayList<>(10);
    private String name;

    public AbstractControl(String prefix, CommandRegistry registry) {
        this.prefix = Objects.requireNonNull(prefix);
        this.registry = Objects.requireNonNull(registry);
    }

    protected void checkInitialized() {
        if (!initialized.get()) {
            throw new NotYetInitializedException(this);
        }
    }

    protected abstract void doHandle(Event event);

    @Override
    public final void handle(Event event) {
        checkInitialized();
        doHandle(event);
        logger.debug("Handled event: {}", event);
    }


    protected Command executeCommand(CommandId commandId) {
        return executeCommand(commandId, null);
    }

    protected Command executeCommand(CommandId downId, String value) {
        return getRegistry().getCommandStack().execute(downId, value);
    }

    CommandRegistry getRegistry() {
        return registry;
    }

    protected List<Command> register(String... parameters) {
        commands.addAll(getRegistry().registerAll(getCommandPrefix(), parameters));
        return commands;
    }

    @Override
    public String getCommandPrefix() {
        return prefix;
    }

    @Override
    public final void init() {
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

    @Override
    public String toString() {
        return "Control{" +
                "prefix='" + prefix + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
