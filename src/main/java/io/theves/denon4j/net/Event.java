/*
 * Copyright 2017 Sascha Theves
 *
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


import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Event is the representation of a receiver`s response.
 *
 * @author stheves
 */
public class Event {
    private final byte[] raw;
    private final String prefix;

    private Event(byte[] raw) {
        this.raw = raw;
        this.prefix = new String(Arrays.copyOfRange(raw, 0, 2), US_ASCII);
    }

    public static Event create(byte[] event) {
        return new Event(event);
    }

    public byte[] getRaw() {
        return raw;
    }

    // TODO delete this
    public boolean startsWith(String prefix) {
        return this.prefix.equals(prefix);
    }

    public String asciiValue() {
        return new String(raw, US_ASCII);
    }
}
