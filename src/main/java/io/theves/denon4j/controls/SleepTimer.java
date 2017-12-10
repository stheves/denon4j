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

import static java.lang.String.format;

public class SleepTimer extends AbstractControl {
    private static final String REGEX = "\\d{3}";
    private static final String SLP = "SLP";
    private static final String OFF = "OFF";

    public SleepTimer(DenonReceiver receiver) {
        super(receiver, SLP);
    }

    public String getTimer() {
        return sendRequest().asciiValue().substring(3);
    }

    public void off() {
        send(OFF);
    }

    public void timer(String timer) {
        if (timer == null || !timer.matches(REGEX)) {
            throw new IllegalArgumentException(format("Timer must match '%s'", REGEX));
        }
        send(timer);
    }
}
