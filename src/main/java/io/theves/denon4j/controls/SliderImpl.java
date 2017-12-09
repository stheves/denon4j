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

import io.theves.denon4j.net.*;


/**
 * SliderImpl control for volumes.
 *
 * @author stheves
 */
public class SliderImpl extends AbstractControl implements Slider {
    private final String up;
    private final String down;

    public SliderImpl(Protocol protocol, String prefix, String up, String down) {
        super(prefix, protocol);
        this.up = up;
        this.down = down;
    }

    @Override
    public void slideUp() {
        executeCommand(up);
    }

    private void executeCommand(String param) {
        Command.createCommand(protocol, prefix, param).execute();
    }

    @Override
    public void slideDown() {
        executeCommand(down);
    }

    @Override
    public String getValue() {
        RequestCommand command = Command.createRequestCommand(protocol, prefix);
        command.execute();
        return command.getResponse().getParameter().getValue();
    }

    @Override
    public void set(String value) {
        executeSetCommand(value);
    }

    private void executeSetCommand(String value) {
        SetCommand setCommand = Command.createSetCommand(protocol, prefix);
        setCommand.set(value);
        setCommand.execute();
    }

    public void doHandle(Event event) {
    }

    public boolean supports(Event event) {
        return getCommandPrefix().equals(event.getPrefix());
    }

    @Override
    protected void doInit() {

    }

}
