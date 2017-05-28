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

import de.theves.denon4j.net.Protocol;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Command extends Event {
    private final Object monitor = new Object();
    private final CommandId id;
    private final Protocol protocol;

    private LocalDateTime lastExecutionTime;

    public Command(Protocol protocol, CommandId id, String prefix, Parameter parameter) {
        super(prefix, parameter);
        this.id = Objects.requireNonNull(id);
        this.protocol = Objects.requireNonNull(protocol);
    }

    public CommandId getId() {
        return id;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public Optional<Event> execute() {
        synchronized (monitor) {
            Optional<Event> result;
            protocol.send(this);
            if (Parameter.REQUEST == getParameter()) {
                result = protocol.recv(200);
            } else {
                result = Optional.empty();
            }
            lastExecutionTime = LocalDateTime.now();
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(id, command.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                "} " + super.toString();
    }
}
