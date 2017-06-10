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

package de.theves.denon4j.controls;

/**
 * Class description.
 *
 * @author stheves
 */
public enum NetControls {
    CURSOR_UP("90"), CURSOR_DOWN("91"), CURSOR_LEFT("92"), CURSOR_RIGHT("93"), ENTER("94"), PLAY("9A"), PAUSE("9B"), STOP("9C"),
    SKIP_PLUS("9D"), SKIP_MINUS("9E"), REPEAT_ONE("9H"), REPEAT_ALL("9I"), REPEAT_OFF("9J"), SHUFFLE_ON("9K"), SHUFFLE_OFF("9M"),
    MODE("9W"), PAGE_NEXT("9X"), PAGE_PREV("9Y"), PARTY_MODE("PT"), INFO_UTF8("E"), INFO_ASCII("A");

    private final String control;

    NetControls(String control) {
        this.control = control;
    }

    public String getControl() {
        return control;
    }

    @Override
    public String toString() {
        return getControl();
    }
}
