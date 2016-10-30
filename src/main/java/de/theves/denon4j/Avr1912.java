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

import de.theves.denon4j.model.Playback;
import de.theves.denon4j.model.Response;
import de.theves.denon4j.model.Sources;
import de.theves.denon4j.net.NetClient;
import de.theves.denon4j.net.TimeoutException;

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
        super(hostname, port);
    }

    public Optional<Response> powerOn() {
        Optional<Response> res = send("PWON");
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

    public Optional<Response> powerOff() {
        return send("PWSTANDBY");
    }

    public boolean isPowerOn() {
        return send("PW?").get().getResponseLines().get(0).equals("PWON");
    }

    public Optional<Response> mute() {
        return send("MUTEON");
    }

    public Optional<Response> unmute() {
        return send("MUTEOFF");
    }

    public boolean isMuted() {
        return send("MUTE?").get().getResponseLines().get(0).equals("MUTE");
    }

    public Response getVolume() {
        return send("MV?").orElseThrow(() -> new TimeoutException("No response within 200ms received."));
    }

    public Optional<Response> volumeUp() {
        return send("MVUP");
    }

    public Optional<Response> volumeDown() {
        return send("MVDOWN");
    }

    public Optional<Response> changeVolume(String value) {
        return send("MV", Optional.of(value));
    }

    public Response getInputSource() {
        return send("SI?").orElseThrow(() -> new TimeoutException("No response within 200ms received."));
    }

    public Optional<Response> selectInputSource(Sources source) {
        return send("SI", Optional.of(source.name()));
    }

    public Optional<Response> selectVideoSource(Sources source) {
        return send("SV", Optional.of(source.name()));
    }

    public Optional<Response> play(Playback playback) {
        return send("SI", Optional.of(playback.name()));
    }

    public OSD createOSD() {
        return new OSD(this);
    }

    public Response isSleepTimerSet() {
        return send("SLP?").orElseThrow(() -> new TimeoutException("No response within 200ms received."));
    }

    public Optional<Response> sleepTimer(String value) {
        return send("SLP", Optional.of(value));
    }

    public Optional<Response> sleepTimerOff() {
        return send("SLPOFF");
    }
}
