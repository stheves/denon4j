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
import de.theves.denon4j.controls.InputControls;
import de.theves.denon4j.controls.OnscreenInfo;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class description.
 *
 * @author stheves
 */
public class InputControl extends AbstractControl {
    private OnscreenInfo mostRecentOnscreenInfo;
    private List<String> paramList;

    public InputControl(CommandRegistry registry) {
        super(registry, "NS");
    }

    @Override
    protected void doInit() {
        InputControls[] params = InputControls.values();
        paramList = new ArrayList<>(params.length);
        paramList.addAll(Stream.of(params).map(Enum::toString).collect(Collectors.toList()));
        register(paramList.toArray(new String[paramList.size()]));
    }

    @Override
    public void doHandle(Event event) {
        if (isOnscreenInformation(event)) {
            if (mostRecentOnscreenInfo == null || mostRecentOnscreenInfo.isComplete()) {
                mostRecentOnscreenInfo = new OnscreenInfo();
            }
            mostRecentOnscreenInfo.addEvent(event);
        }

    }

    @Override
    protected RequestCommand getRequestCommand() {
        // TODO find the request command by prefix
        return (RequestCommand) getRegistry().getCommand(getCommands().get(getCommands().size() - 1).getId());
    }

    private boolean isOnscreenInformation(Event event) {
        // TODO support also NSA events
        return event.build().signature().startsWith("NSE");
    }

    public OnscreenInfo getOnscreenInfo() {
        readOnscreenInfo();
        return mostRecentOnscreenInfo;
    }

    private void readOnscreenInfo() {
        getState();
        // wait until all events are received
        while (mostRecentOnscreenInfo == null || !mostRecentOnscreenInfo.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void control(InputControls controls) {
        executeCommand(getCommands().get(paramList.indexOf(controls.toString())).getId());
    }
}
