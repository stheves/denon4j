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

import java.util.Arrays;
import java.util.List;

/**
 * Represents the content of the info display.
 *
 * @author Sascha Theves
 */
public class DisplayInfo {
    private List<InfoListEntry> informationList;
    private int cursorPosition;
    private String title;

    public DisplayInfo(String title, List<InfoListEntry> infoList, int cursorPos) {
        this.title = title;
        this.informationList = infoList;
        this.cursorPosition = cursorPos;
    }

    public String getTitle() {
        return title;
    }

    public List<InfoListEntry> getInformationList() {
        return informationList;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public String toString() {
        return "DisplayInfo{" +
                "informationList=" + Arrays.toString(informationList.toArray()) +
                ", cursorPosition=" + cursorPosition +
                ", title='" + title + '\'' +
                '}';
    }
}
