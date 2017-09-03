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

package de.theves.denon4j.controls;

import de.theves.denon4j.internal.net.Command;
import de.theves.denon4j.internal.net.Event;
import de.theves.denon4j.net.Protocol;

import java.util.List;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetworkControlImpl extends AbstractControl implements de.theves.denon4j.controls.NetworkControl {
    private OnscreenInfo mostRecentOnscreenInfo;
    private List<String> paramList;

    public NetworkControlImpl(Protocol protocol) {
        super("NS", protocol);
        setName("Network USB/AUDIO/IPOD Extended Control");
    }

    @Override
    public void doHandle(Event event) {
        if (isOnscreenInformation(event)) {
            if (mostRecentOnscreenInfo == null || mostRecentOnscreenInfo.isComplete()) {
                // init a new info message
                mostRecentOnscreenInfo = new OnscreenInfo();
            }
            mostRecentOnscreenInfo.addEvent(event);
        }

    }

    @Override
    protected void doInit() {
    }

    private boolean isOnscreenInformation(Event event) {
        return event.getPrefix().startsWith("NS");
    }

    @Override
    public OnscreenInfo getOnscreenInfo() {
        readOnscreenInfo();
        return mostRecentOnscreenInfo;
    }

    private void readOnscreenInfo() {
        Command nse = Command.createCommand(protocol, prefix, "E");
        nse.execute();
        // wait until all events are received
        while (mostRecentOnscreenInfo == null || !mostRecentOnscreenInfo.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public void control(NetworkControls controls) {
        Command command = Command.createCommand(protocol, prefix, controls.toString());
        command.execute();
    }
}
