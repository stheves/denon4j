/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.theves.denon4j.internal.net;

import de.theves.denon4j.controls.InvalidSignatureException;
import de.theves.denon4j.net.Signature;
import de.theves.denon4j.internal.PatternValidator;
import de.theves.denon4j.net.Parameter;

import java.util.regex.Pattern;

/**
 * Class description.
 *
 * @author stheves
 */
public class ParameterImpl implements Parameter {
    public static final Parameter EMPTY = new ParameterImpl("");
    public static final Parameter REQUEST = new ParameterImpl("?");

    private final PatternValidator validator;

    private String value;

    public ParameterImpl(String value) {
        this.value = value;
        this.validator = new PatternValidator(Pattern.compile(".*"));
        validate();
    }

    @Override
    public String getValue() {
        return value;
    }

    public static Parameter createParameter(String val) {
        if (REQUEST.getValue().equals(val)) {
            return REQUEST;
        }
        return new ParameterImpl(val);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public Signature build() {
        return this::getValue;
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
