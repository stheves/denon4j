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


/**
 * A slider on the receiver e.g. the master volume slider.
 *
 * @author stheves
 */
public class Slider extends AbstractControl {
    private final String up;
    private final String down;

    public Slider(DenonReceiver receiver, String prefix, String up, String down) {
        super(receiver, prefix);
        this.up = up;
        this.down = down;
    }

    public void slideUp() {
        send(up);
    }

    public void slideDown() {
        send(down);
    }

    public String getValue() {
        // do not return the MAX values
        return sendRequest(getCommandPrefix() + "\\d\\d\\d?").asciiValue().substring(2);
    }

    public void set(String value) {
        send(value);
    }
}
