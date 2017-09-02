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
import de.theves.denon4j.controls.Select;
import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SelectImpl control.
 *
 * @author stheves
 */
public class SelectImpl<S extends Enum> extends StatefulControl implements Select<S> {
    private final S[] params;

    private List<String> paramList;

    public SelectImpl(CommandRegistry registry, String prefix, S[] params) {
        super(registry, prefix);
        this.params = Objects.requireNonNull(params);
    }

    @Override
    protected void doInit() {
        paramList = new ArrayList<>(params.length + 1); // +1 for request parameter
        paramList.addAll(Stream.of(params).map(Enum::toString).collect(Collectors.toList()));
        paramList.add(ParameterImpl.REQUEST.getValue());
        register(paramList.toArray(new String[paramList.size()]));
    }

    @Override
    protected RequestCommand getRequestCommand() {
        Command command = getRegistry().getCommand(getCommands().get(getCommands().size() - 1).getId());
        if (command instanceof RequestCommand) {
            return (RequestCommand) command;
        }
        throw new IllegalStateException("Request command not found");
    }

    @Override
    public void source(S source) {
        executeCommand(getCommands().get(paramList.indexOf(source.toString())).getId());
    }

    @Override
    public S getSource() {
        Parameter state = getState();
        return findSource(state);
    }

    private S findSource(Parameter state) {
        return Stream.of(params)
                .filter(e -> state.getValue().equals(e.toString()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
