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

import de.theves.denon4j.model.Control;
import de.theves.denon4j.model.Event;
import de.theves.denon4j.net.EventConsumer;
import de.theves.denon4j.net.Protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable generic receiver class that acts as the base class for all receiver implementations.
 * Offers the basic connection handling as well as a generic method to send commands to the receiver.
 * <p>
 * This class is intended to be subclassed by clients who want to implement their own receiver model.
 *
 * @author Sascha Theves
 */
public class GenericController implements EventConsumer {

    protected final Protocol client;
    protected final CommandRegistry registry;
    private final Map<Class, Control> controls;

    /**
     * Creates a receiver with the given net client. The client is used for the communication with the receiver.
     *
     * @param client the client to use.
     */
    public GenericController(Protocol client, CommandRegistry registry) {
        this.client = Objects.requireNonNull(client);
        this.registry = Objects.requireNonNull(registry);
        this.controls = Collections.synchronizedMap(new HashMap<>(10));
        this.client.addEventConsumer(this);
    }

    public void addControl(Control ctrl) {
        if (null != ctrl) {
            controls.put(ctrl.getClass(), ctrl);
        }
    }

    public void removeControl(Control ctrl) {
        if (null != ctrl) {
            controls.remove(ctrl.getClass());
        }
    }

    public <T> T adapt(Class<T> cls) {
        return (T) controls.get(cls);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (client.isConnected()) {
            // seems that somebody has forgotten to do his housekeeping.
            // try to clean up now.
            client.disconnect();
        }

    }

    @Override
    public void onEvent(Event event) {
        controls.values().stream().filter(control1 -> control1.getCommandPrefix().equals(event.getPrefix())).
                forEach(control -> control.handle(event));
    }
}
