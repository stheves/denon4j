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

import de.theves.denon4j.controls.*;
import de.theves.denon4j.net.EventDispatcher;
import de.theves.denon4j.net.Protocol;
import de.theves.denon4j.net.Tcp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static de.theves.denon4j.controls.SwitchState.*;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class AVR1912 implements AVR {
    private final EventDispatcher eventDispatcher;
    private final Protocol protocol;
    private final Collection<Control> controls;
    private ToggleImpl powerToggle;
    private SliderImpl masterSlider;
    private ToggleImpl mainZoneToggle;
    private ToggleImpl muteToggle;
    private SelectImpl<InputSource> selectInput;
    private SelectImpl<VideoSource> selectVideo;
    private NetworkControlImpl selectNet;
    private Menu menu;

    public AVR1912(String host, int port) {
        this(new Tcp(host, port));
    }

    AVR1912(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol);
        this.eventDispatcher = new EventDispatcher();
        this.controls = new ArrayList<>();

        this.protocol.setDispatcher(eventDispatcher);

        addControls(this.controls);
        addToDispatcher(this.controls);
    }

    private void addControls(Collection<? super Control> controls) {
        // power control
        powerToggle = new ToggleImpl(protocol, "PW", ON, STANDBY);
        powerToggle.setName("Power Switch");
        powerToggle.init();
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new SliderImpl(protocol, "MV", "UP", "DOWN");
        masterSlider.setName("Master Volume");
        masterSlider.init();
        controls.add(masterSlider);

        // mute control
        muteToggle = new ToggleImpl(protocol, "MU", ON, OFF);
        muteToggle.setName("Mute Toggle");
        muteToggle.init();
        controls.add(muteToggle);

        // source input
        selectInput = new SelectImpl<>(protocol, "SI", InputSource.values());
        selectInput.setName("Select INPUT Source");
        selectInput.init();
        controls.add(selectInput);

        // source video
        selectVideo = new SelectImpl<>(protocol, "SV", VideoSource.values());
        selectVideo.setName("Select VIDEO Source");
        selectVideo.init();
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new ToggleImpl(protocol, "ZM", ON, OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        mainZoneToggle.init();
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        selectNet = new NetworkControlImpl(protocol);
        selectNet.init();
        controls.add(selectNet);

        menu = new Menu(protocol);
        menu.init();
        controls.add(menu);
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.stream().forEach(eventDispatcher::addControl);
    }

    EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public Toggle power() {
        return powerToggle;
    }

    public Toggle mainZone() {
        return mainZoneToggle;
    }

    public Slider masterVolume() {
        return masterSlider;
    }

    public Toggle mute() {
        return muteToggle;
    }

    public Select<InputSource> input() {
        return selectInput;
    }

    public NetworkControl networkControl() {
        return selectNet;
    }

    public Select<VideoSource> video() {
        return selectVideo;
    }

    public Menu menu() {
        return menu;
    }

    @Override
    public void printHelp(PrintStream writer) {

    }

    @Override
    public Collection<Control> getControls() {
        return controls;
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    public void disconnect() {
        getControls().forEach(eventDispatcher::removeControl);
        getControls().forEach(Control::dispose);
        protocol.disconnect();
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }
}
