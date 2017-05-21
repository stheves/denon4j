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

package de.theves.denon4j.model;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Event {
    private final String prefix;
    private final Parameter parameter;
    private final byte[] raw;

    private Event(byte[] raw) {
        this.raw = Objects.requireNonNull(raw);
        String rawStr = new String(raw, StandardCharsets.US_ASCII);
        this.prefix = rawStr.substring(0, 2);
        this.parameter = new Parameter(rawStr.substring(2));
    }

    public Event(String raw) {
        this(raw.getBytes(StandardCharsets.US_ASCII));
    }

    public String getPrefix() {
        return prefix;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public byte[] getRaw() {
        return raw;
    }

    public String build() {
        return getPrefix() + getParameter().build();
    }

    @Override
    public String toString() {
        return "Event{" +
                "prefix='" + prefix + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
