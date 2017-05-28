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

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public abstract class Switch extends AbstractControl {
    private final String onValue;
    private final String offValue;

    protected CommandId onId;
    protected CommandId offId;
    protected CommandId requestId;

    public Switch(CommandRegistry registry, String prefix, String onValue, String offValue) {
        super(registry, prefix);
        this.onValue = onValue;
        this.offValue = offValue;
    }

    protected void switchOff() {
        executeCommand(offId);
    }

    protected void switchOn() {
        executeCommand(onId);
    }

    public boolean switchedOn() {
        return onValue.equals(getState().getValue());
    }

    public boolean switchedOff() {
        return offValue.equals(getState().getValue());
    }

    public String getOnValue() {
        return onValue;
    }

    public String getOffValue() {
        return offValue;
    }

    @Override
    protected void doInit() {
        registerCommands();
    }

    private void registerCommands() {
        List<Command> commands = register(onValue, offValue, ParameterImpl.REQUEST.getValue());
        onId = commands.get(0).getId();
        offId = commands.get(1).getId();
        requestId = commands.get(2).getId();
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(requestId);
    }
}
