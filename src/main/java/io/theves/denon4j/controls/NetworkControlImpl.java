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

package io.theves.denon4j.controls;

import io.theves.denon4j.net.Command;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.Protocol;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetworkControlImpl extends SelectImpl<NetworkControls> implements NetworkControl {
    private DisplayInfo mostRecentDisplayInfo;

    public NetworkControlImpl(Protocol protocol) {
        super(protocol, "NS", NetworkControls.values());
        setName("Network USB/AUDIO/IPOD Extended Control");
    }

    @Override
    public void doHandle(Event event) {
        if (isDisplayInfoEvent(event)) {
            if (mostRecentDisplayInfo == null || mostRecentDisplayInfo.isComplete()) {
                // init a new info message
                mostRecentDisplayInfo = new DisplayInfo();
            }
            mostRecentDisplayInfo.addEvent(event);
        }

    }

    @Override
    protected void doInit() {
    }

    private boolean isDisplayInfoEvent(Event event) {
        return event.getPrefix().startsWith("NS");
    }

    @Override
    public DisplayInfo getDisplay() {
        readOnscreenInfo();
        return mostRecentDisplayInfo;
    }

    private void readOnscreenInfo() {
        Command nse = Command.createCommand(protocol, prefix, "E");
        nse.execute();
        // wait until all events are received
        while (mostRecentDisplayInfo == null || !mostRecentDisplayInfo.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
