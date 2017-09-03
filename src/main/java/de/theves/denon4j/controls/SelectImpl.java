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

import de.theves.denon4j.internal.net.Command;
import de.theves.denon4j.internal.net.Event;
import de.theves.denon4j.internal.net.Parameter;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.Protocol;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * SelectImpl control.
 *
 * @author stheves
 */
public class SelectImpl<S extends Enum> extends AbstractControl implements Select<S> {
    private final S[] params;

    public SelectImpl(Protocol protocol, String prefix, S[] params) {
        super(prefix, protocol);
        this.params = Objects.requireNonNull(params);
    }

    @Override
    public void source(S source) {
        Command.createCommand(protocol, prefix, source.toString()).execute();
    }

    @Override
    public S getSource() {
        RequestCommand command = Command.createRequestCommand(protocol, prefix);
        command.execute();
        Event received = command.getReceived();
        return findSource(received.getParameter());
    }

    private S findSource(Parameter state) {
        return Stream.of(params)
                .filter(e -> state.getValue().equals(e.toString()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    protected void doHandle(Event event) {

    }

    @Override
    protected void doInit() {
    }
}
