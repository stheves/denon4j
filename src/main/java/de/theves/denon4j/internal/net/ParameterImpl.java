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

import de.theves.denon4j.net.Parameter;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class ParameterImpl implements Parameter {
    public static final Parameter EMPTY = new ParameterImpl("");
    public static final Parameter REQUEST = new ParameterImpl("?");

    private String value;


    public ParameterImpl(String value) {
        if (null == value || value.trim().length() > 25) {
            throw new IllegalArgumentException("Name cannot be null or greater than 25 chars");
        }
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ParameterImpl{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public String build() {
        return getValue();
    }

    public static Parameter create(String name) {
        if (REQUEST.getValue().equals(name)) {
            return REQUEST;
        }
        return new ParameterImpl(name);
    }
}
