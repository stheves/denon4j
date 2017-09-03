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

import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetworkControlImpl extends AbstractControl implements de.theves.denon4j.controls.NetworkControl {
    private OnscreenInfo mostRecentOnscreenInfo;
    private List<String> paramList;

    public NetworkControlImpl(CommandRegistry registry) {
        super("NS", registry);
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
        NetworkControls[] params = NetworkControls.values();
        paramList = new ArrayList<>(params.length);
        paramList.addAll(Stream.of(params).map(Enum::toString).collect(Collectors.toList()));
        register(paramList.toArray(new String[paramList.size()]));
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
        Command nse = getCommands().stream().filter(cmd -> cmd.signature().equals("NSE")).findFirst().orElseThrow(() -> new CommandNotFoundException("NSE command not found"));
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
        executeCommand(getCommands().get(paramList.indexOf(controls.toString())).getId());
    }
}
