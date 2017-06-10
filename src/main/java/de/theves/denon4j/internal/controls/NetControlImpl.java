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

package de.theves.denon4j.internal.controls;

import de.theves.denon4j.controls.CommandRegistry;
import de.theves.denon4j.controls.Message;
import de.theves.denon4j.controls.NetControls;
import de.theves.denon4j.net.Event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetControlImpl extends SelectImpl<NetControls> {
    private Message mostRecentMessage;
    private AtomicBoolean readingMessage = new AtomicBoolean(false);

    public NetControlImpl(CommandRegistry registry) {
        super(registry, "NS", NetControls.values(), false);
    }

    @Override
    public void handle(Event event) {
        super.handle(event);
        if (isOnscreenInformation(event)) {
            if (readingMessage.compareAndSet(false, true)) {
                mostRecentMessage = new Message();
            }
            mostRecentMessage.addEvent(event);
        } else {
            // we assume that the first non-onscreen event is the message pause
            readingMessage.set(false);
        }

    }

    private boolean isOnscreenInformation(Event event) {
        return event.build().signature().startsWith("NSE");
    }

    public Message getMostRecentMessage() {
        return mostRecentMessage;
    }

}
