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
import de.theves.denon4j.controls.Valid;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class description.
 *
 * @author stheves
 */
public class PatternValidator implements Valid {
    private final Pattern pattern;
    private String value;

    public PatternValidator(Pattern p) {
        pattern = Objects.requireNonNull(p);
    }

    public boolean matches(String value) {
        this.value = value;
        return isValid();
    }

    public void checkPattern(String value) {
        this.value = value;
        validate();
    }

    @Override
    public void validate() throws InvalidSignatureException {
        if (!isValid()) {
            throw new InvalidSignatureException(value, pattern);
        }
    }

    @Override
    public boolean isValid() {
        return pattern.matcher(value).matches();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
