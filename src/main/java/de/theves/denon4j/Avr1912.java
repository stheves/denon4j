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

import static de.theves.denon4j.Commands.*;
import static de.theves.denon4j.Parameters.*;

/**
 * This class implements the control protocol for the Denon AVR 1912.
 *
 * @author Sascha Theves
 */
public class Avr1912 extends AbstractAvReceiver {

    public Avr1912(String hostname, int port) {
        this(hostname, port, 3000);
    }

    public Avr1912(String hostname, int port, int timeToWait) {
        super(hostname, port, timeToWait);
    }

    public Response powerOn() {
        Response res = send(PW, ON);
        // as specification - K) 1 seconds later, please
        // transmit the next COMMAND after transmitting a
        // power on COMMAND （ PWON ）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        return res;
    }

    public Response powerOff() {
        return send(PW, STANDBY);
    }

    public boolean isPowerOn() {
        return send(PW, STATUS).getResponseLines().get(0)
                .equals(PW.toString() + ON.toString());
    }

    public Response mute() {
        return send(MUTE, ON);
    }

    public Response unmute() {
        return send(MUTE, OFF);
    }

    public boolean isMuted() {
        return send(MUTE, STATUS).getResponseLines().get(0).equals(MUTE.toString());
    }

    public Response getVolume() {
        return send(VOL, STATUS);
    }

    public Response volumeUp() {
        return send(VOL, UP);
    }

    public Response volumeDown() {
        return send(VOL, DOWN);
    }

    public Response changeVolume(String value) {
        return send(VOL, value);
    }

    public Response getInputSource() {
        return send(SELECT_INPUT, STATUS);
    }

    public Response selectInputSource(Sources source) {
        return send(SELECT_INPUT, source.toString());
    }

    public Response selectVideoSource(Sources source) {
        return send(SELECT_VIDEO, source.toString());
    }

    public Response play(Playback playback) {
        return send(SELECT_INPUT, playback.toString());
    }

    public OSD createOSD() {
        return new OSD(this);
    }

    public Response isSleepTimerSet() {
        return send(Commands.SLP, Parameters.STATUS);
    }

    public Response sleepTimer(String value) {
        return send(Commands.SLP, value);
    }

    public Response sleepTimerOff() {
        return send(Commands.SLP, OFF);
    }
}
