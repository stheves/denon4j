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
import de.theves.denon4j.net.Parameter;

import java.util.regex.Pattern;

/**
 * Mutable implementation of an {@link Parameter}.
 *
 * @author stheves
 */
public class MutableParameter implements Parameter {
    private final PatternValidator validator;
    private String value;

    public MutableParameter(Pattern pattern) {
        this.validator = new PatternValidator(pattern);
    }

    /**
     * Sets the value of this parameter and validates it with
     * {@link de.theves.denon4j.controls.Valid#validate()} before setting.
     *
     * @param val the value to set.
     * @throws InvalidSignatureException if input is invalid.
     */
    public void setValue(String val) throws InvalidSignatureException {
        validator.validate(val);
        this.value = val;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Signature build() {
        return getValue() != null ? this::getValue : () -> "";
    }

    @Override
    public boolean isValid() {
        return validator.isValid(getValue());
    }

    @Override
    public void validate() throws InvalidSignatureException {
        validator.validate(getValue());
    }
}
