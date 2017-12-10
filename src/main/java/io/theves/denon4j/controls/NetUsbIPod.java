/*
 * Copyright 2017 Sascha Theves
 *
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

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Class description.
 *
 * @author stheves
 */
public class NetUsbIPod extends AbstractControl {
    private static final String CURSOR_UP = "90";
    private static final String CURSOR_DOWN = "91";
    private static final String CURSOR_LEFT = "92";
    private static final String CURSOR_RIGHT = "93";
    private static final String PLAY = "9A";
    private static final String PAUSE = "9B";
    private static final String STOP = "9C";
    private static final String ENTER = "94";
    private static final String PAGE_PREV = "9Y";
    private static final String PAGE_NEXT = "9X";
    private static final String SHUFFLE_ON = "9K";
    private static final String SHUFFLE_OFF = "9M";
    private static final String MODE = "9W";
    private static final String REPEAT_ONE = "9H";
    private static final String REPEAT_ALL = "9I";
    private static final String REPEAT_OFF = "9J";
    private static final String PARTY_MODE = "PT";
    private static final String SKIP_PLUS = "9D";
    private static final String SKIP_MINUS = "9E";

    private OsdInfoList mostRecentOsdInfoList;
    private boolean europeModel;

    public NetUsbIPod(DenonReceiver receiver, boolean europeModel) {
        super(receiver, "NS");
        setName("Network Audio/USB /iPod DIRECT Extended Control");
        this.europeModel = europeModel;
    }

    @Override
    public void doHandle(Event event) {
        if (isDisplayInfoEvent(event)) {
            mostRecentOsdInfoList.addEvent(event);
        }
    }


    private boolean isDisplayInfoEvent(Event event) {
        return event.startsWith(getCommandPrefix());
    }

    public OsdInfoList getDisplay() {
        readOnscreenInfo();
        return mostRecentOsdInfoList;
    }


    public void cursorUp() {
        send(CURSOR_UP);
    }


    public void cursorDown() {
        send(CURSOR_DOWN);
    }


    public void cursorLeft() {
        send(CURSOR_LEFT);
    }


    public void cursorRight() {
        send(CURSOR_RIGHT);
    }


    public void play() {
        send(PLAY);
    }


    public void pause() {
        send(PAUSE);
    }


    public void stop() {
        send(STOP);
    }


    public void enter() {
        send(ENTER);
    }


    public void previousPage() {
        send(PAGE_PREV);
    }


    public void nextPage() {
        send(PAGE_NEXT);
    }


    public void shuffleOn() {
        send(SHUFFLE_ON);
    }


    public void shuffleOff() {
        send(SHUFFLE_OFF);
    }


    public void mode() {
        send(MODE);
    }


    public void repeatOne() {
        send(REPEAT_ONE);
    }

    public void repeatAll() {
        send(REPEAT_ALL);
    }


    public void repeatOff() {
        send(REPEAT_OFF);
    }


    public void partyMode() {
        send(PARTY_MODE);
    }


    public void skipPlus() {
        send(SKIP_PLUS);
    }


    public void skipMinus() {
        send(SKIP_MINUS);
    }

    private void readOnscreenInfo() {
        mostRecentOsdInfoList = new OsdInfoList(europeModel ? UTF_8 : US_ASCII);
        sendAndReceive(europeModel ? "E" : "A", res -> res.size() == 9);
    }
}
