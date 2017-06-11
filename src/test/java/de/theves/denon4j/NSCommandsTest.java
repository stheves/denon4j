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

import de.theves.denon4j.controls.Line;
import de.theves.denon4j.internal.net.EventFactory;
import de.theves.denon4j.net.Event;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Class description.
 *
 * @author stheves
 */
public class NSCommandsTest {
    @Test
    public void testParseDataByte() {
        byte dataByte = 0b00001001; // playable music + cursor select bit set
        Event event = EventFactory.create("NSE1" + (char) dataByte + "Come Away With Mö");
        Line aLine = new Line(event);
        assertThat(aLine.getDisplayLine()).isEqualTo("Come Away With Mö");
        assertThat(aLine.getIndex()).isEqualTo(1);
        assertThat(aLine.isCursorSelect()).isTrue();
        assertThat(aLine.isDirectory()).isFalse();
        assertThat(aLine.isPicture()).isFalse();
        assertThat(aLine.isPlayable()).isTrue();
    }

    @Test
    public void testParseWithoutDataByte() {
        Event event = EventFactory.create("NSE0Now Playing Usb");
        Line aLine = new Line(event);
        assertThat(aLine.getDisplayLine()).isEqualTo("Now Playing Usb");
        assertThat(aLine.getIndex()).isEqualTo(0);
        assertThat(aLine.isCursorSelect()).isFalse();
        assertThat(aLine.isDirectory()).isFalse();
        assertThat(aLine.isPicture()).isFalse();
        assertThat(aLine.isPlayable()).isFalse();
    }
}
