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

package de.theves.denon4j.internal;

import de.theves.denon4j.controls.InvalidSignatureException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class description.
 *
 * @author stheves
 */
public class PatternValidator {
    private final Pattern pattern;

    public PatternValidator(Pattern p) {
        pattern = Objects.requireNonNull(p);
    }

    public void validate(String value) throws InvalidSignatureException {
        if (!isValid(value)) {
            throw new InvalidSignatureException(value, pattern);
        }
    }

    public boolean isValid(String value) {
        return value != null && pattern.matcher(value).matches();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
