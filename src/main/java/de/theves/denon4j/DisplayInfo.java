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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Represents the Onscreen Display Information List as requested by the NSA/NSE commands.
 *
 * @author Sascha Theves
 */
public class DisplayInfo {
    private static final int SKIP_ELEMENTS = 4;
    private final Response rawResponse;
    private String humandReadable;
    private String cursorPosition;
    private List<String> directories;
    private boolean picture;
    private String nowPlaying;

    public DisplayInfo(Response res) {
        this.rawResponse = res;
        this.directories = new ArrayList<>();
        parseDataByte();
    }

    public String humanReadable() {
        if (null == humandReadable) {
            StringBuilder builder = new StringBuilder();
            this.rawResponse.getResponseLines().stream().forEach(s -> {
                IntStream chars = s.chars();
                chars.skip(SKIP_ELEMENTS).filter(chr -> chr != 0).forEach(filtered -> builder.append((char) filtered));
                builder.append(System.lineSeparator());
            });
            humandReadable = builder.toString();
        }
        return humandReadable;
    }

    private void parseDataByte() {
        this.rawResponse.getResponseLines().stream().forEach(line -> {
            String lineValue = getValue(line);
            byte dataByte = line.getBytes()[4]; // 5th byte is the data byte
            final BitSet set = BitSet.valueOf(new byte[]{dataByte}); // read the bits
            if (set.get(3)) {
                this.cursorPosition = lineValue;
            }

            if (set.get(1)) {
                this.directories.add(lineValue);
            }

            if (set.get(6)) {
                this.picture = true;
            }

            if (set.get(0)) {
                this.nowPlaying = lineValue;
            }
        });
    }

    public List<String> getDirectories() {
        return directories;
    }

    public String getCursorPosition() {
        return cursorPosition;
    }

    public String getNowPlaying() {
        return nowPlaying;
    }


    public boolean isPicture() {
        return this.picture;
    }


    private String getValue(String line) {
        StringBuilder builder = new StringBuilder();
        IntStream chars = line.chars();
        chars.skip(SKIP_ELEMENTS).filter(chr -> chr != 0).forEach(filtered -> builder.append((char) filtered));
        return builder.toString().trim();
    }
}
