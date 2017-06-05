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
import de.theves.denon4j.controls.InvalidSignatureException;
import de.theves.denon4j.controls.Slider;
import de.theves.denon4j.internal.PatternValidator;
import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.RequestCommand;

import java.util.List;
import java.util.regex.Pattern;


/**
 * SliderImpl control for volumes.
 *
 * @author stheves
 */
public class SliderImpl extends AbstractControl implements Slider {
    private final String up;
    private final String down;
    private final PatternValidator validator;

    private CommandId upId;
    private CommandId downId;
    private CommandId setId;
    private CommandId requestId;

    public SliderImpl(CommandRegistry registry, String prefix, String up, String down, Pattern pattern) {
        super(registry, prefix);
        this.up = up;
        this.down = down;
        validator = new PatternValidator(pattern);
    }

    @Override
    protected void doInit() {
        List<Command> commands = register(up, down, "[" + validator.getPattern().pattern() + "]", ParameterImpl.REQUEST.getValue());
        upId = commands.get(0).getId();
        downId = commands.get(1).getId();
        setId = commands.get(2).getId();
        requestId = commands.get(3).getId();
    }

    @Override
    public void slideUp() {
        executeCommand(upId);
    }

    @Override
    public void slideDown() {
        executeCommand(downId);
    }

    @Override
    public String getValue() {
        return getState().getValue();
    }

    @Override
    public void set(String value) {
        executeCommand(setId, value);
    }

    @Override
    public void validate() throws InvalidSignatureException {
        if (!isValid()) {
            throw new InvalidSignatureException(getValue(), validator.getPattern());
        }
    }

    @Override
    public boolean isValid() {
        // check current value for validity
        return validator.isValid(getValue());
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(requestId);
    }

    @Override
    public void handle(Event event) {
        // check for pattern
        if (validator.isValid((String) event.getParameter().getValue())) {
            // handle only valid ones otherwise we would have an invalid <code>state</code>.
            super.handle(event);
        }
    }
}
