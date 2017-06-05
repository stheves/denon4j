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

import de.theves.denon4j.controls.InvalidSignatureException;
import de.theves.denon4j.controls.Signature;
import de.theves.denon4j.internal.PatternValidator;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.Parameter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class description.
 *
 * @author stheves
 */
public class EventImpl implements Event {
    private final String prefix;
    private final Parameter parameter;
    private final LocalDateTime createdAt;
    private final PatternValidator validator;

    protected EventImpl(String prefix, Parameter parameter) {
        this.prefix = Objects.requireNonNull(prefix);
        this.parameter = Objects.requireNonNull(parameter);
        this.createdAt = LocalDateTime.now();
        validator = new PatternValidator(Pattern.compile("(\\w\\w)")); // validate only the prefix
    }

    @Override
    public boolean isValid() {
        return validator.matches(this.prefix) && parameter.isValid();
    }

    @Override
    public void validate() throws InvalidSignatureException {
        // check all valid
        validator.validate();
        parameter.validate();
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public Signature build() {
        return () -> getPrefix() + getParameter().build().signature();
    }

    @Override
    public String toString() {
        return "Event{" +
                "prefix='" + prefix + '\'' +
                ", parameter=" + parameter.build().signature() +
                ", createdAt=" + createdAt +
                '}';
    }

    public static Event create(String event) {
        return new EventImpl(event.substring(0, 2), ParameterImpl.createParameter(event.substring(2)));
    }
}
