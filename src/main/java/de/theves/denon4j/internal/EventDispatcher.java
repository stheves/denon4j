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

import de.theves.denon4j.Control;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.EventListener;
import de.theves.denon4j.net.Protocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author Sascha Theves
 */
public class EventDispatcher implements EventListener {

    private final Collection<Control> controls;
    private final Protocol protocol;

    /**
     */
    public EventDispatcher(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol);
        this.controls = new HashSet<>();
    }

    public void addControl(Control ctrl) {
        if (null != ctrl) {
            controls.add(ctrl);
        }
    }

    public void removeControl(Control ctrl) {
        if (null != ctrl) {
            controls.remove(ctrl);
        }
    }

    public Collection<Control> getControls() {
        return controls;
    }

    @Override
    public void onEvent(Event event) {
        controls.stream().filter(ctrl ->
                ctrl.isInitialized() && ctrl.getCommandPrefix().equals(event.getPrefix())).
                forEach(control -> control.handle(event));
    }

    public void startDispatching() {
        this.protocol.setListener(this);
    }
}
