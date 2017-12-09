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

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.Protocol;

import static io.theves.denon4j.controls.NetUsbControls.*;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetUsbIPod extends AbstractControl {
    private DisplayInfo mostRecentDisplayInfo;

    public NetUsbIPod(DenonReceiver receiver) {
        super("NS", receiver);
        setName("Network USB/AUDIO/IPOD Extended Control");
    }

    @Override
    public void doHandle(Event event) {
        if (isDisplayInfoEvent(event)) {
            if (mostRecentDisplayInfo == null || mostRecentDisplayInfo.isComplete()) {
                // init a new info message
                mostRecentDisplayInfo = new DisplayInfo();
            }
            mostRecentDisplayInfo.addEvent(event);
        }
    }

    private boolean isDisplayInfoEvent(Event event) {
        return event.getPrefix().startsWith("NS");
    }

    public DisplayInfo getDisplay() {
        readOnscreenInfo();
        return mostRecentDisplayInfo;
    }


    public void cursorUp() {
        send(CURSOR_UP.getControl());
    }


    public void cursorDown() {
        send(CURSOR_DOWN.getControl());
    }


    public void cursorLeft() {
        send(CURSOR_LEFT.getControl());
    }


    public void cursorRight() {
        send(CURSOR_RIGHT.getControl());
    }


    public void play() {
        send(PLAY.getControl());
    }


    public void pause() {
        send(PAUSE.getControl());
    }


    public void stop() {
        send(STOP.getControl());
    }


    public void enter() {
        send(ENTER.getControl());
    }


    public void previousPage() {
        send(PAGE_PREV.getControl());
    }


    public void nextPage() {
        send(PAGE_NEXT.getControl());
    }


    public void shuffleOn() {
        send(SHUFFLE_ON.getControl());
    }


    public void shuffleOff() {
        send(SHUFFLE_OFF.getControl());
    }


    public void mode() {
        send(MODE.getControl());
    }


    public void repeatOne() {
        send(REPEAT_ONE.getControl());
    }

    public void repeatAll() {
        send(REPEAT_ALL.getControl());
    }


    public void repeatOff() {
        send(REPEAT_OFF.getControl());
    }


    public void partyMode() {
        send(PARTY_MODE.getControl());
    }


    public void skipPlus() {
        send(SKIP_PLUS.getControl());
    }


    public void skipMinus() {
        send(SKIP_MINUS.getControl());
    }

    private void readOnscreenInfo() {
        send("E");
        // wait until all events are received
        while (mostRecentDisplayInfo == null || !mostRecentDisplayInfo.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
