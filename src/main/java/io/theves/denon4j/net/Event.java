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

package io.theves.denon4j.net;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event is the representation of a receiver`s response.
 *
 * @author stheves
 */
public class Event {
    private final String prefix;
    private final String parameter;
    private final LocalDateTime createdAt;

    protected Event(String prefix, String parameter) {
        this.prefix = Objects.requireNonNull(prefix);
        this.parameter = Objects.requireNonNull(parameter);
        this.createdAt = LocalDateTime.now();
    }

    public static Event create(String event) {
        return new Event(event.substring(0, 2), event.substring(2));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getParameter() {
        return parameter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Event{" +
            "prefix='" + prefix + '\'' +
            ", parameter=" + parameter +
            ", createdAt=" + createdAt +
            '}';
    }

    public byte[] getRaw() {
        return new StringBuilder()
            .append(getPrefix())
            .append(getParameter())
            .toString()
            .getBytes(StandardCharsets.UTF_8);
    }
}
