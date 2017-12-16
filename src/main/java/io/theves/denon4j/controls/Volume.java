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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;

import static java.lang.String.format;

public class Volume extends Slider {
    private static final String REGEX = "\\d\\d\\d?";
    private String max;

    public Volume(DenonReceiver receiver, String prefix, String up, String down) {
        super(receiver, prefix, up, down);
    }

    public void set(String value) {
        if (value == null || !value.matches(REGEX)) {
            throw new IllegalArgumentException(format("Value must match '%s'", REGEX));
        }
        send(value);
    }

    @Override
    public void doHandle(Event event) {
        if (event.startsWith(getCommandPrefix()) && event.asciiValue().contains("MAX")) {
            max = event.asciiValue().substring(5);
        }
    }

    public String getMax() {
        return max;
    }
}
