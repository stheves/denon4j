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

import java.util.List;

import static de.theves.denon4j.Value.NULL;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Toggle extends ControlAdapter {
    private final String on;
    private final String off;
    private CommandId onId;
    private CommandId offId;
    private CommandId requestId;

    public Toggle(CommandRegistry registry, String prefix, String on, String off) {
        super(registry, prefix);
        this.on = on;
        this.off = off;
    }

    public void toggle() {
        if (on.equals(getState())) {
            executeCommand(offId, NULL);
        }
        if (off.equals(getState())) {
            executeCommand(onId, NULL);
        }
    }

    public boolean isOn() {
        return on.equals(getState());
    }

    public boolean isOff() {
        return off.equals(getState());
    }

    @Override
    public void init() {
        registerCommands();
    }

    private void registerCommands() {
        List<Command> commands = register(on, off, Parameter.REQUEST.getName());
        onId = commands.get(0).getId();
        offId = commands.get(1).getId();
        requestId = commands.get(2).getId();
    }

    @Override
    public CommandId getRequestId() {
        return requestId;
    }
}
