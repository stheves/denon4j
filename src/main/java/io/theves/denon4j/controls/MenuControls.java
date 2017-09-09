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

package io.theves.denon4j.controls;

/**
 * Class description.
 *
 * @author stheves
 */
public enum MenuControls {
    CURSOR_UP("CUP"),
    CURSOR_DOWN("CDN"),
    CURSOR_LEFT("CLT"),
    CURSOR_RIGHT("CRT"),
    ENTER("ENT"),
    RETURN("RTN"),
    MENU_ON("MEN ON"),
    MENU_OFF("MEN OFF"),
    SOURCE_ON("SRC ON"),
    SOURCE_OFF("SRC OFF");


    private final String control;

    MenuControls(String control) {
        this.control = control;
    }

    @Override
    public String toString() {
        return getControl();
    }

    public String getControl() {
        return control;
    }
}
