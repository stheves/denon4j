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

package de.theves.denon4j.controls;

import de.theves.denon4j.internal.net.AlreadyInitException;
import de.theves.denon4j.internal.net.Event;
import de.theves.denon4j.net.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected final Protocol protocol;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private String name;

    public AbstractControl(String prefix, Protocol protocol) {
        this.prefix = Objects.requireNonNull(prefix);
        this.protocol = protocol;
    }


    @Override
    public final void handle(Event event) {
        checkInitialized();
        doHandle(event);
        logger.debug("Handled event: {}", event);
    }

    protected void checkInitialized() {
        if (!initialized.get()) {
            throw new NotYetInitializedException(this);
        }
    }

    protected abstract void doHandle(Event event);

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
    public void dispose() {
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
