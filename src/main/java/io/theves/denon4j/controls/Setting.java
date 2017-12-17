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

/**
 * Can be used to set different settings of the receiver.
 *
 * @author stheves
 */
public class Setting extends AbstractControl {

    public Setting(DenonReceiver receiver, String prefix) {
        super(receiver, prefix);
    }

    /**
     * Sets the given value.
     *
     * @param value the value to set.
     */
    public void set(String value) {
        send(value);
    }

    /**
     * The setting`s value.
     *
     * @return the value.
     */
    public String get() {
        Event received = sendRequest();
        return received.asciiValue().substring(2);
    }
}
