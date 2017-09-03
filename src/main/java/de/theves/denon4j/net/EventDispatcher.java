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

package de.theves.denon4j.net;

import de.theves.denon4j.controls.Control;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Receives events from an AVR and dispatches them to the controls.
 *
 * @author stheves
 */
public class EventDispatcher {

    private final Collection<Control> controls;
    private final Set<Event> unhandledEvents;

    /**
     */
    public EventDispatcher() {
        this.controls = new HashSet<>();
        unhandledEvents = new HashSet<>();
    }

    public Set<Event> getUnhandledEvents() {
        return Collections.unmodifiableSet(unhandledEvents);
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

    public void dispatch(Event event) {
        List<Control> supporters = controls.stream().filter(ctrl ->
                ctrl.supports(event)).collect(Collectors.toList());
        if (supporters.size() == 0) {
            // nobody cares...
            unhandledEvents.add(event);
        } else {
            supporters.forEach(ctrl -> ctrl.handle(event));
        }
    }
}
