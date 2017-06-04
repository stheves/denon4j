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

import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.Parameter;

import java.util.Objects;

/**
 * Class description.
 *
 * @author stheves
 */
public class EventImpl implements Event {
    private final String prefix;
    private final Parameter parameter;

    protected EventImpl(String prefix, Parameter parameter) {
        this.prefix = Objects.requireNonNull(prefix);
        this.parameter = Objects.requireNonNull(parameter);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public String build() {
        return getPrefix() + getParameter().build();
    }

    @Override
    public String toString() {
        return "EventImpl{" +
                "prefix='" + prefix + '\'' +
                ", parameter=" + parameter +
                '}';
    }

    public static Event create(String event) {
        return new EventImpl(event.substring(0, 2), ParameterImpl.create(event.substring(2)));
    }
}
