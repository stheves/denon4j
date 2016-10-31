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

package de.theves.denon4j.model;

/**
 * Volume parameter represents the receivers volume.
 *
 * @author Sascha Theves
 */
public class Volume extends Parameter {
    private Float floatValue;

    public Volume(String vol) {
        super(vol);
        if (!vol.matches("\\d\\d\\d?")) {
            throw new IllegalArgumentException("Volume value must be in the form 'ddd' e.g. '565'.");
        }
        String floatValueAsStr = vol.substring(0, 2);
        if (vol.length() == 3) {
            floatValueAsStr += "." + vol.charAt(vol.length() - 1);
        }

        floatValue = new Float(floatValueAsStr);

        if (floatValue > 99) {
            throw new IllegalArgumentException("Volume cannot be higher than 99.");
        }
    }

    public Float getFloatValue() {
        return floatValue;
    }
}
