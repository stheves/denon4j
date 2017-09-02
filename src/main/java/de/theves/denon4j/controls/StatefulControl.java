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

import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.*;


/**
 * Class description.
 *
 * @author stheves
 */
public abstract class StatefulControl extends AbstractControl {
    private static final Parameter DIRTY = ParameterImpl.createParameter("DIRTY");

    private Parameter state = DIRTY;

    public StatefulControl(CommandRegistry registry, String prefix) {
        super(prefix, registry);
    }

    @Override
    public void doHandle(Event event) {
        state = event.getParameter();
    }

    @Override
    protected Command executeCommand(CommandId downId, String value) {
        Command cmd = super.executeCommand(downId, value);
        if (cmd.isDirtying()) {
            state = StatefulControl.DIRTY;
        }
        return cmd;
    }

    public Parameter getState() {
        checkInitialized();
        refreshState();
        return state;
    }

    private void refreshState() {
        if (state == DIRTY) {
            logger.debug("Refreshing dirty state");
            executeCommand(getRequestCommand().getId());
            Event event = getRequestCommand().getReceived();
            state = event.getParameter();
            logger.debug("State refreshed: {}", state);
        }
    }

    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getCommands()
                .stream()
                .filter(cmd -> cmd instanceof RequestCommand)
                .findFirst()
                .orElseThrow(() -> new CommandNotFoundException("No request command found"));
    }


}
