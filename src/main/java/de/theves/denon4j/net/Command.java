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

package de.theves.denon4j.net;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class description.
 *
 * @author stheves
 */
public class Command extends Event {
    protected final Protocol protocol;
    private final String signature;
    private LocalDateTime executedAt = LocalDateTime.MIN;

    public Command(Protocol protocol, String prefix, Parameter parameter) {
        super((prefix + parameter.getValue()).getBytes(StandardCharsets.US_ASCII), prefix, parameter);
        this.protocol = Objects.requireNonNull(protocol);
        this.signature = getPrefix() + getParameter().getValue();
    }

    public static SetCommand createSetCommand(Protocol protocol, String prefix) {
        return new SetCommand(protocol, prefix);
    }

    public static Command createCommand(Protocol protocol, String command) {
        if (command == null || command.length() < 2) {
            throw new IllegalArgumentException("Command length must be > 2");
        }
        return createCommand(protocol, command.substring(0, 2), command.substring(2));
    }

    public static Command createCommand(Protocol protocol, String prefix, String param) {
        if (Parameter.REQUEST.getValue().equals(param)) {
            return createRequestCommand(protocol, prefix);
        }
        return new Command(protocol, prefix, Parameter.createParameter(param));
    }

    public static RequestCommand createRequestCommand(Protocol protocol, String prefix) {
        return new RequestCommand(protocol, prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(signature, command.signature);
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void execute() {
        doSend();
        executedAt = LocalDateTime.now();
    }

    protected void doSend() {
        protocol.send(this);
    }

    @Override
    public String toString() {
        return "Command{" + signature() + '}';
    }

    public String signature() {
        return signature;
    }
}
