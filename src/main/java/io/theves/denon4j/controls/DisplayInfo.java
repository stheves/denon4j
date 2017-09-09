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


import io.theves.denon4j.net.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class description.
 *
 * @author stheves
 */
public class DisplayInfo {
    private final LinkedHashMap<Integer, Line> lines;

    public DisplayInfo() {
        lines = new LinkedHashMap<>();
    }

    public void addEvent(Event event) {
        Line line = new Line(event);
        lines.put(line.getIndex(), line);

    }

    public Line lineAt(int index) {
        return lines.get(index);
    }

    public Line getCursor() {
        return lines.values().stream().filter(line -> line.isCursorSelect()).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("=======DISPLAY INFO=======" + System.lineSeparator());
        builder.append(System.lineSeparator());
        getLines().stream().forEach(line -> builder.append(line.getDisplayLine()).append(System.lineSeparator()));
        builder.append(System.lineSeparator());
        builder.append("=======END=======" + System.lineSeparator());
        return builder.toString();
    }

    public List<Line> getLines() {
        List<Line> lines = new ArrayList<>(this.lines.values());
        Collections.sort(lines);
        return lines;
    }

    public boolean isComplete() {
        return lines.size() == 9;
    }
}
