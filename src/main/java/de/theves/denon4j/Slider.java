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

import de.theves.denon4j.internal.AbstractControl;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Parameter;

import java.util.List;


/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Slider extends AbstractControl {
    private final String up;
    private final String down;
    private final String set;

    private CommandId upId;
    private CommandId downId;
    private CommandId setId;
    private CommandId requestId;

    public Slider(CommandRegistry registry, String prefix, String up, String down, String set) {
        super(registry, prefix);
        this.up = up;
        this.down = down;
        this.set = set;
    }

    @Override
    protected void doInit() {
        List<Command> commands = register(up, down, set, Parameter.REQUEST.getValue());
        upId = commands.get(0).getId();
        downId = commands.get(1).getId();
        setId = commands.get(2).getId();
        requestId = commands.get(3).getId();
    }

    public void slideUp() {
        executeCommand(upId);
    }

    public void slideDown() {
        executeCommand(downId);
    }

    public String getValue() {
        return getState().getValue();
    }

    public void set(String value) {
        executeCommand(setId, value);
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(requestId);
    }
}
