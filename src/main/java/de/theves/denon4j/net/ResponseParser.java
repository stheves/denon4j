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

import de.theves.denon4j.EmptyResponseException;
import de.theves.denon4j.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses responses to different model types.
 *
 * @author Sascha Theves
 */
public class ResponseParser {
    public SurroundMode parseSurroundMode(Optional<Response> res) {
        checkResponse(res);
        Event volValue = findFirstMatch(res.get(), "MS");
        return SurroundMode.valueOf(volValue.getMessage().substring(2));

    }

    public DigitalInputMode parseDigitalInputMode(Optional<Response> res) {
        checkResponse(res);
        Event volValue = findFirstMatch(res.get(), "DC");
        return DigitalInputMode.valueOf(volValue.getMessage().substring(2));
    }

    public Volume parseVolume(Optional<Response> res) {
        checkResponse(res);
        Event volValue = findFirstMatch(res.get(), "MV");
        return new Volume(volValue.getMessage().substring(2));
    }

    public DisplayInfo parseDisplayInfo(Optional<Response> res) {
        checkResponse(res);
        List<Event> events = res.get().getEvents();
        String title = events.get(0).getMessage();
        List<InfoListEntry> infoListEntries = new ArrayList<>();
        int cursorPos = parseCursorPosition(events.get(events.size() - 1));

        // start with second, skip last
        int loop = 0;
        for (int i = 1; i < events.size() - 1; i++) {
            String message = events.get(i).getMessage();
            if (!message.startsWith("NS")) {
                // indicates the current position
                InfoListEntry last = infoListEntries.get(loop - 1);
                InfoListEntry replaced = new InfoListEntry(last.getInfo() + message);
                infoListEntries.set(loop - 1, replaced);
            } else {
                infoListEntries.add(new InfoListEntry(message));
            }
            loop++;
        }

        DisplayInfo di = new DisplayInfo(title, infoListEntries, cursorPos);
        return di;
    }

    private int parseCursorPosition(Event event) {
        String message = event.getMessage();
        int start = message.indexOf('[');
        int end = message.lastIndexOf(']');
        String pageInfo = message.substring(start + 1, end);
        String[] pageInfoParts = pageInfo.split("/");
        if (pageInfoParts.length != 2) {
            // urgh
            throw new ParseException("Invalid pagination info. Expected line contains [x/y] but found " + pageInfo);
        }
        try {
            // subtracted by one to get index cursor position
            return Integer.parseInt(pageInfoParts[0].trim()) - 1;
        } catch (NumberFormatException nfe) {
            throw new ParseException("Cannot parse pagination info.", nfe);
        }
    }

    private Event findFirstMatch(Response response, String parameter) {
        Optional<Event> first = response.getEvents().stream().filter(event ->
                event.getMessage().startsWith(parameter)
        ).findFirst();
        checkResult(first, parameter);
        return first.get();
    }

    private void checkResponse(Optional<Response> res) {
        if (!res.isPresent()) {
            throw new EmptyResponseException("Reciever did not return a response.");
        }
    }

    private <T> void checkResult(Optional<T> result, String paramter) {
        if (!result.isPresent()) {
            throw new IllegalStateException("Couldn`t find '" + paramter + "'.");
        }
    }
}
