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

package io.theves.denon4j.net;

import java.util.Objects;

/**
 * Class description.
 *
 * @author stheves
 */
public class Command extends Event {
    private final String signature;

    public Command(String prefix, String parameter) {
        super(prefix, parameter);
        this.signature = getPrefix() + getParameter();
    }

    public static Command createCommand(String command) {
        if (command == null || command.length() < 2) {
            throw new IllegalArgumentException("Command length must be > 2");
        }
        return createCommand(command.substring(0, 2), command.substring(2));
    }

    public static Command createCommand(String prefix, String param) {
        return new Command(prefix, param);
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

    @Override
    public String toString() {
        return "Command{" + signature() + '}';
    }

    public String signature() {
        return signature;
    }
}
