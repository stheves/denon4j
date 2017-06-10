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

import de.theves.denon4j.net.Event;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Class description.
 *
 * @author stheves
 */
public class OnscreenInfo {
    private final LinkedHashMap<String, Event> events;

    public OnscreenInfo() {
        events = new LinkedHashMap<>();
    }

    public void addEvent(Event event) {
        if (!event.build().signature().startsWith("NSE")) {
            throw new IllegalArgumentException("Only NSE events are supported at the moment");
        }
        events.put(getIndex(event), event);

        // TODO check 5th byte of raw data, this must be the cursor&playable information data byte
    }

    private String getIndex(Event event) {
        return event.getParameter().getValue().substring(1, 2);
    }

    public Collection<Event> getEvents() {
        return events.values();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("=======MESSAGE=======\r");
        events.values().stream().forEach(event -> builder.append(event.getParameter().build().signature()));
        builder.append("=======END=======\r");
        return builder.toString();
    }
}
