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

import de.theves.denon4j.net.Protocol;
import de.theves.denon4j.net.Tcp;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Avr1912 implements Receiver {
    private final Dispatcher dispatcher;
    private final Protocol protocol;
    private final CommandRegistry registry;
    private Toggle powerToggle;
    private Fader masterFader;

    public Avr1912(String host, int port) {
        protocol = new Tcp(host, port);
        registry = new CommandRegistry(protocol);
        dispatcher = new Dispatcher(protocol);
        addControls();
    }

    // Begin Controls
    public boolean isPowerOn() {
        return powerToggle.isOn();
    }

    public void togglePower() {
        powerToggle.toggle();
    }

    public void masterVolUp() {
        masterFader.fadeUp();
    }

    public void masterVolDown() {
        masterFader.fadeDown();
    }

    public Value getMasterVol() {
        return masterFader.fader();
    }

    public void setMasterVol(Value vol) {
        masterFader.set(vol);
    }
    // End Controls

    @Override
    public void printHelp() {
        registry.printCommands(System.out);
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
    }

    public void disconnect() {
        dispatcher.getControls().stream().forEach(Control::dispose);
        protocol.disconnect();
    }

    private void addControls() {
        // power control
        powerToggle = new Toggle(registry, "PW", "ON", "STANDBY");
        dispatcher.addControl(powerToggle);

        // master vol. control
        masterFader = new Fader(registry, "MV", "UP", "DOWN", "[000-999]");
        dispatcher.addControl(masterFader);

        // center vol. control

    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
