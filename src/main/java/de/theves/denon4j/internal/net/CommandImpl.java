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

package de.theves.denon4j.internal.net;

import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.Protocol;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class description.
 *
 * @author stheves
 */
public class CommandImpl extends EventImpl implements Command {
    protected final Protocol protocol;
    private final CommandId id;
    private LocalDateTime executedAt = LocalDateTime.MIN;

    public CommandImpl(Protocol protocol, CommandId id, String prefix, Parameter parameter) {
        super((prefix + parameter.getValue()).getBytes(StandardCharsets.US_ASCII), prefix, parameter);
        this.id = Objects.requireNonNull(id);
        this.protocol = Objects.requireNonNull(protocol);
    }

    @Override
    public CommandId getId() {
        return id;
    }

    @Override
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    @Override
    public void execute() {
        doSend();
        executedAt = LocalDateTime.now();
    }

    @Override
    public boolean isDirtying() {
        return true;
    }

    @Override
    public String signature() {
        return getPrefix() + getParameter().getValue();
    }

    protected void doSend() {
        protocol.send(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandImpl command = (CommandImpl) o;
        return Objects.equals(id, command.id);
    }

    @Override
    public String toString() {
        return "Command{" + signature() + '}';
    }
}
