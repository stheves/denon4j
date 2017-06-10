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
import de.theves.denon4j.net.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetControlImpl extends AbstractControl {
    private Message mostRecentMessage;
    private AtomicBoolean readingMessage = new AtomicBoolean(false);
    private List<String> paramList;

    public NetControlImpl(CommandRegistry registry) {
        super(registry, "NS");
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

    @Override
    protected void doInit() {
        NetControls[] params = NetControls.values();
        paramList = new ArrayList<>(params.length + 1); // +1 for request parameter
        paramList.addAll(Stream.of(params).map(Enum::toString).collect(Collectors.toList()));
        paramList.add("E");
        register(paramList.toArray(new String[paramList.size()]));
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(getCommands().get(getCommands().size() - 1).getId());
    }

    private boolean isOnscreenInformation(Event event) {
        return event.build().signature().startsWith("NSE");
    }

    public Message getMostRecentMessage() {
        getState();
        return mostRecentMessage;
    }

    public void control(NetControls controls) {
        executeCommand(getCommands().get(paramList.indexOf(controls.toString())).getId());
    }
}
