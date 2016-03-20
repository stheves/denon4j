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

import static de.theves.denon4j.Commands.ONSCREEN_CONTROL;
import static de.theves.denon4j.Commands.ONSCREEN_DISPLAY_INFO_UTF8;
import static de.theves.denon4j.Parameters.*;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class OSD {
    private AbstractAvReceiver receiver;

    public OSD(AbstractAvReceiver receiver) {
        this.receiver = receiver;
    }

    public DisplayInfo getDisplayInfo() {
        return new DisplayInfo(this.receiver.send(ONSCREEN_DISPLAY_INFO_UTF8, NONE));
    }

    public void moveCursorDown() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, CURSOR_DOWN, null);
    }

    public void moveCursorUp() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, CURSOR_UP, null);
    }

    public void moveCursorLeft() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, CURSOR_LEFT, null);
    }

    public void moveCursorRight() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, CURSOR_RIGHT, null);
    }

    public void enter() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, ENTER, null);
    }

    public void show() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, GUI_MENU_ON, null);
    }

    public void hide() {
        this.receiver.sendOnly(ONSCREEN_CONTROL, GUI_MENU_OFF, null);
    }
}
