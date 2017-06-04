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

import de.theves.denon4j.internal.net.Tcp;
import de.theves.denon4j.net.Protocol;

import java.io.PrintStream;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class Avr1912 implements Receiver {
    private final EventDispatcher eventDispatcher;
    private final Protocol protocol;
    private final CommandRegistry registry;
    private Toggle powerToggle;
    private Slider masterSlider;
    private Toggle muteToggle;
    private SelectInput selectInput;
    private SelectVideo selectVideo;

    public Avr1912(String host, int port) {
        protocol = new Tcp(host, port);
        registry = new CommandRegistry(protocol);
        eventDispatcher = new EventDispatcher(protocol);
        addControls();
    }

    public Toggle power() {
        return powerToggle;
    }

    public Slider masterVolume() {
        return masterSlider;
    }

    public Toggle mute() {
        return muteToggle;
    }

    public void selectInput(InputSource source) {
        selectInput.select(source);
    }

    public void selectVideo(VideoSource source) {
        selectVideo.select(source);
    }

    @Override
    public void printHelp(PrintStream writer) {
        registry.printCommands(writer);
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
        eventDispatcher.startDispatching();
    }

    public void disconnect() {
        eventDispatcher.getControls().stream().forEach(Control::dispose);
        eventDispatcher.getControls().clear();
        protocol.disconnect();
    }

    private void addControls() {
        // power control
        powerToggle = new Toggle(registry, "PW", "ON", "STANDBY");
        powerToggle.init();
        eventDispatcher.addControl(powerToggle);

        // master vol. control
        masterSlider = new Slider(registry, "MV", "UP", "DOWN", "[000-999]");
        masterSlider.init();
        eventDispatcher.addControl(masterSlider);

        // mute control
        muteToggle = new Toggle(registry, "MU", "ON", "OFF");
        muteToggle.init();
        eventDispatcher.addControl(muteToggle);

        // select input
        selectInput = new SelectInput(registry);

        // select video
        selectVideo = new SelectVideo(registry);
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
