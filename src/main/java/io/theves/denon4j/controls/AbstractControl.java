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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;

import java.util.Objects;

/**
 * Base class for controls that handles requests/responses.
 *
 * @author stheves
 */
public abstract class AbstractControl implements Control {
    protected final String prefix;
    private final DenonReceiver receiver;

    private String name;

    public AbstractControl(String prefix, DenonReceiver receiver) {
        this.prefix = Objects.requireNonNull(prefix);
        this.receiver = receiver;
    }

    protected void send(String param) {
        receiver.send(prefix + param);
    }

    protected Event sendRequest() {
        return receiver.send(prefix + "?");
    }

    @Override
    public final void handle(Event event) {
        doHandle(event);
    }

    protected abstract void doHandle(Event event);

    @Override
    public String getCommandPrefix() {
        return prefix;
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

    @Override
    public String toString() {
        return "Control{" +
            "prefix='" + prefix + '\'' +
            ", name='" + name + '\'' +
            '}';
    }


}
