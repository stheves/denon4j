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

import de.theves.denon4j.model.DigitalInputMode;
import de.theves.denon4j.model.Event;
import de.theves.denon4j.model.Response;
import de.theves.denon4j.model.SurroundMode;

import java.util.List;
import java.util.Optional;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class ResponseParser {
    public Optional<SurroundMode> pMS(Response response) {
        List<Event> events = response.getEvents();
        for (Event event : events) {
            String message = event.getMessage();
            if (message.startsWith("MS")) {
                return Optional.of(SurroundMode.valueOf(message.substring(2)));
            }
        }
        return Optional.empty();
    }

    public Optional<DigitalInputMode> pDC(Response response) {
        List<Event> events = response.getEvents();
        for (Event event : events) {
            String message = event.getMessage();
            if (message.startsWith("DC")) {
                return Optional.of(DigitalInputMode.valueOf(message.substring(2)));
            }
        }
        return Optional.empty();
    }
}
