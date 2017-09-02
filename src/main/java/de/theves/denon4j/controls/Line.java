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

import de.theves.denon4j.net.Event;

/**
 * Class description.
 *
 * @author stheves
 */
public class Line implements Comparable<Line> {
    private Event event;
    private String displayLine;
    private boolean playable;
    private boolean directory;
    private boolean cursorSelect;
    private boolean picture;
    private Integer index;

    public Line(Event event) {
        this.event = event;
        parseDataByte(this.event);
    }

    private void parseDataByte(Event event) {
        byte[] raw = event.getRaw();
        if (raw.length > 4 && isDataByte(raw[4])) {
            byte data = raw[4];
            // data byte available
            playable = isSet(data, 0);
            directory = isSet(data, 1);
            cursorSelect = isSet(data, 3);
            picture = isSet(data, 6);
        }
        this.displayLine = event.getParameter().getValue().substring(2);
        this.index = Integer.valueOf(event.getParameter().getValue().substring(1, 2));
    }

    private boolean isDataByte(byte data) {
        return !Character.isLetterOrDigit(data);
    }

    private boolean isSet(byte data, int pos) {
        return (data >> pos & 1) == 1;
    }

    public String getDisplayLine() {
        return displayLine;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isPlayable() {
        return playable;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isCursorSelect() {
        return cursorSelect;
    }

    public boolean isPicture() {
        return picture;
    }

    @Override
    public int compareTo(Line o) {
        return getIndex().compareTo(o.getIndex());
    }

    public Integer getIndex() {
        return index;
    }
}
