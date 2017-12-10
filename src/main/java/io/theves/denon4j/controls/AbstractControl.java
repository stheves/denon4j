/*
 * Copyright 2017 Sascha Theves
 *
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

package io.theves.denon4j.controls;

import io.theves.denon4j.CompletionCallback;
import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.EventListener;

import java.util.List;
import java.util.Objects;

/**
 * Base class for controls that handles requests/responses.
 *
 * @author stheves
 */
public abstract class AbstractControl implements EventListener {
    private final String commandPrefix;
    private final DenonReceiver receiver;

    private String name;

    public AbstractControl(DenonReceiver receiver, String commandPrefix) {
        this.commandPrefix = Objects.requireNonNull(commandPrefix);
        this.receiver = receiver;
    }

    protected final void send(String param) {
        receiver.send(commandPrefix + param);
    }

    protected final Event sendRequest(String regex) {
        return receiver.sendRequest(getCommandPrefix() + "?", regex);
    }

    protected final Event sendRequest() {
        return sendRequest(getCommandPrefix() + ".*");
    }

    final List<Event> sendAndReceive(String param, CompletionCallback completionCallback) {
        return receiver.sendAndReceive(getCommandPrefix() + param, completionCallback);
    }

    @Override
    public final void handle(Event event) {
        if (shouldHandle(event)) {
            doHandle(event);
        }
    }

    private boolean shouldHandle(Event event) {
        return event.startsWith(getCommandPrefix());
    }

    protected void doHandle(Event event) {
        // subclasses may override
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Control{" +
            "commandPrefix='" + commandPrefix + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
