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
