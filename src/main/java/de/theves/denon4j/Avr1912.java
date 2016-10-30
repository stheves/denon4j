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

import de.theves.denon4j.net.NetClient;

import java.util.Optional;

/**
 * This class implements the control protocol for the Denon AVR 1912.
 *
 * @author Sascha Theves
 */
public class Avr1912 extends AbstractAvReceiver {

    public Avr1912(NetClient client) {
        super(client);
    }

    public Avr1912(String hostname, int port) {
        this(hostname, port, 3000);
    }

    public Avr1912(String hostname, int port, int timeToWait) {
        super(hostname, port, timeToWait);
    }

    public Response powerOn() {
        Response res = send("PWON");
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
        return send("PWSTANDBY");
    }

    public boolean isPowerOn() {
        return send("PW?").getResponseLines().get(0).equals("PWON");
    }

    public Response mute() {
        return send("MUTEON");
    }

    public Response unmute() {
        return send("MUTEOFF");
    }

    public boolean isMuted() {
        return send("MUTE?").getResponseLines().get(0).equals("MUTE");
    }

    public Response getVolume() {
        return send("MV?");
    }

    public Response volumeUp() {
        return send("MVUP");
    }

    public Response volumeDown() {
        return send("MVDOWN");
    }

    public Response changeVolume(String value) {
        return send("MV", Optional.of(value));
    }

    public Response getInputSource() {
        return send("SI?");
    }

    public Response selectInputSource(Sources source) {
        return send("SI", Optional.of(source.name()));
    }

    public Response selectVideoSource(Sources source) {
        return send("SV", Optional.of(source.name()));
    }

    public Response play(Playback playback) {
        return send("SI", Optional.of(playback.name()));
    }

    public OSD createOSD() {
        return new OSD(this);
    }

    public Response isSleepTimerSet() {
        return send("SLP?");
    }

    public Response sleepTimer(String value) {
        return send("SLP", Optional.of(value));
    }

    public Response sleepTimerOff() {
        return send("SLPOFF");
    }
}
