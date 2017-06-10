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

package de.theves.denon4j.internal.net;

import de.theves.denon4j.net.Event;

import java.nio.charset.StandardCharsets;

/**
 * Class description.
 *
 * @author stheves
 */
public class EventFactory {
    public static Event create(String event) {
        return create(event.getBytes(StandardCharsets.US_ASCII));
    }

    public static Event create(byte[] raw) {
        String prefix = new String(raw, 0, 2, StandardCharsets.US_ASCII);
        String parameter = new String(raw, 2, raw.length - 2, StandardCharsets.UTF_8);
        return new RawEventImpl(raw, prefix, ParameterImpl.createParameter(parameter));
    }
}
