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

import io.theves.denon4j.net.Command;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.Protocol;

/**
 * Class description.
 *
 * @author stheves
 */
public class NetUsbImpl extends AbstractControl implements NetUsb {
    private DisplayInfo mostRecentDisplayInfo;

    public NetUsbImpl(Protocol protocol) {
        super("NS", protocol);
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

    @Override
    protected void doInit() {
    }

    private boolean isDisplayInfoEvent(Event event) {
        return event.getPrefix().startsWith("NS");
    }

    @Override
    public DisplayInfo getDisplay() {
        readOnscreenInfo();
        return mostRecentDisplayInfo;
    }

    @Override
    public void cursorUp() {
        Command.createCommand(protocol, prefix, NetUsbControls.CURSOR_UP.getControl()).execute();
    }

    @Override
    public void cursorDown() {
        Command.createCommand(protocol, prefix, NetUsbControls.CURSOR_DOWN.getControl()).execute();
    }

    @Override
    public void cursorLeft() {
        Command.createCommand(protocol, prefix, NetUsbControls.CURSOR_LEFT.getControl()).execute();
    }

    @Override
    public void cursorRight() {
        Command.createCommand(protocol, prefix, NetUsbControls.CURSOR_RIGHT.getControl()).execute();
    }

    @Override
    public void play() {
        Command.createCommand(protocol, prefix, NetUsbControls.PLAY.getControl()).execute();
    }

    @Override
    public void pause() {
        Command.createCommand(protocol, prefix, NetUsbControls.PAUSE.getControl()).execute();
    }

    @Override
    public void stop() {
        Command.createCommand(protocol, prefix, NetUsbControls.STOP.getControl()).execute();
    }

    @Override
    public void enter() {
        Command.createCommand(protocol, prefix, NetUsbControls.ENTER.getControl()).execute();
    }

    @Override
    public void previousPage() {
        Command.createCommand(protocol, prefix, NetUsbControls.PAGE_PREV.getControl()).execute();
    }

    @Override
    public void nextPage() {
        Command.createCommand(protocol, prefix, NetUsbControls.PAGE_NEXT.getControl()).execute();
    }

    @Override
    public void shuffleOn() {
        Command.createCommand(protocol, prefix, NetUsbControls.SHUFFLE_ON.getControl()).execute();
    }

    @Override
    public void shuffleOff() {
        Command.createCommand(protocol, prefix, NetUsbControls.SHUFFLE_OFF.getControl()).execute();
    }

    @Override
    public void mode() {
        Command.createCommand(protocol, prefix, NetUsbControls.MODE.getControl()).execute();
    }

    @Override
    public void repeatOne() {
        Command.createCommand(protocol, prefix, NetUsbControls.REPEAT_ONE.getControl()).execute();
    }

    @Override
    public void repeatAll() {
        Command.createCommand(protocol, prefix, NetUsbControls.REPEAT_ALL.getControl()).execute();
    }

    @Override
    public void repeatOff() {
        Command.createCommand(protocol, prefix, NetUsbControls.REPEAT_OFF.getControl()).execute();
    }

    @Override
    public void partyMode() {
        Command.createCommand(protocol, prefix, NetUsbControls.PARTY_MODE.getControl()).execute();
    }

    @Override
    public void skipPlus() {
        Command.createCommand(protocol, prefix, NetUsbControls.SKIP_PLUS.getControl()).execute();
    }

    @Override
    public void skipMinus() {
        Command.createCommand(protocol, prefix, NetUsbControls.SKIP_MINUS.getControl()).execute();
    }

    private void readOnscreenInfo() {
        Command nse = Command.createCommand(protocol, prefix, "E");
        nse.execute();
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
