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

package de.theves.denon4j;

enum Parameters {
    ON("ON"), STANDBY("STANDBY"), OFF("OFF"), STATUS("?"), UP("UP"),
    DOWN("DOWN"), CURSOR_UP("CUP"), CURSOR_DOWN("CDN"), CURSOR_LEFT("CLT"),
    CURSOR_RIGHT("CRT"), ENTER("ENT"), RETURN("RTN"), GUI_MENU_ON("MEN ON"), GUI_MENU_OFF("MEN OFF"),
    GUI_SOURCE_SELECT_ON("SRC ON"), GUI_SOURCE_SELECT_OFF("SRC OFF"), NONE("");

    private String name;

    Parameters(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
