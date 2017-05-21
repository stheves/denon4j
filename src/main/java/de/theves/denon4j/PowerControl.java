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

package de.theves.denon4j;

import de.theves.denon4j.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class PowerControl implements Control {
    public static final String PREFIX = "PW";

    private final List<Command> commandList;
    private final Receiver receiver;

    private Power power;

    public PowerControl(Receiver receiver) {
        this.commandList = new ArrayList<>();
        this.receiver = receiver;
        registerCommands();
    }

    private void registerCommands() {
        commandList.addAll(receiver.getCommandRegistry().registerCommands("PW", "ON", "STANDBY", "?"));
    }

    @Override
    public String getCommandPrefix() {
        return PREFIX;
    }

    @Override
    public Collection<Command> commandList() {
        return commandList;
    }

    @Override
    public void handle(Event event) {
        power = Power.valueOf(event.getParameter().getName());
    }

    public void toggle() {
        switch (power) {
            case ON:
                doToggle(Power.STANDBY);
                break;
            case STANDBY:
                doToggle(Power.ON);
                break;
            default:
                throw new IllegalStateException("Unknown state: " + power);
        }
    }

    private void doToggle(Power power) {
        Command command = receiver.getCommandRegistry().getCommand(CommandId.from(PREFIX + power.name()));
        receiver.getCommandStack().execute(command);
    }

    public void requestState() {
        Command command = receiver.getCommandRegistry().getCommand(CommandId.from(PREFIX + "?"));
        receiver.getCommandStack().execute(command);
        // TODO handle async correctly
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {


        }
    }

    public Power getState() {
        requestState();
        return power;
    }
}
