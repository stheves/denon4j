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
import de.theves.denon4j.internal.EventDispatcher;
import de.theves.denon4j.internal.controls.CommandRegistryImpl;
import de.theves.denon4j.internal.controls.SelectImpl;
import de.theves.denon4j.internal.controls.SliderImpl;
import de.theves.denon4j.internal.controls.ToggleImpl;
import de.theves.denon4j.internal.net.Tcp;
import de.theves.denon4j.net.Protocol;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import static de.theves.denon4j.controls.SwitchState.*;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class Avr1912 implements AVR {
    private static final Pattern PATTERN_MASTER_VOL = Pattern.compile("[0-9][0-9][5]?");

    private final EventDispatcher eventDispatcher;
    private final Protocol protocol;
    private final Collection<Control> controls;

    private final CommandRegistry registry;
    private Toggle powerToggle;
    private Slider masterSlider;
    private Toggle muteToggle;
    private Select<InputSource> selectInput;
    private Select<VideoSource> selectVideo;
    private Toggle mainZoneToggle;
    private Select<ExtendedSettings> selectNet;

    public Avr1912(String host, int port) {
        this(new Tcp(host, port));
    }

    public Avr1912(Protocol protocol) {
        this.protocol = protocol;
        this.registry = new CommandRegistryImpl(protocol);
        this.eventDispatcher = new EventDispatcher(protocol);
        this.controls = new ArrayList<>();
        addControls(this.controls);
        addToDispatcher(this.controls);
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.stream().forEach(eventDispatcher::addControl);
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

    public Select<InputSource> selectInput() {
        return selectInput;
    }

    public Select<ExtendedSettings> selectNetworkControl() {
        return selectNet;
    }

    public Select<VideoSource> selectVideo() {
        return selectVideo;
    }

    @Override
    public void printHelp(PrintStream writer) {
        registry.printCommands(writer);
    }

    @Override
    public Collection<Control> getControls() {
        return controls;
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
        eventDispatcher.startDispatching();
    }

    public void disconnect() {
        getControls().forEach(eventDispatcher::removeControl);
        getControls().forEach(Control::dispose);
        protocol.disconnect();
    }

    private void addControls(Collection<Control> controls) {
        // power control
        powerToggle = new ToggleImpl(registry, "PW", ON, STANDBY);
        powerToggle.setName("Power Switch");
        powerToggle.init();
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new SliderImpl(registry, "MV", "UP", "DOWN", PATTERN_MASTER_VOL);
        masterSlider.setName("Master Volume");
        masterSlider.init();
        controls.add(masterSlider);

        // mute control
        muteToggle = new ToggleImpl(registry, "MU", ON, OFF);
        muteToggle.setName("Mute Toggle");
        muteToggle.init();
        controls.add(muteToggle);

        // select input
        selectInput = new SelectImpl<>(registry, "SI", InputSource.values(), true);
        selectInput.setName("Select INPUT Source");
        selectInput.init();
        controls.add(selectInput);

        // select video
        selectVideo = new SelectImpl<>(registry, "SV", VideoSource.values(), true);
        selectVideo.setName("Select VIDEO Source");
        selectVideo.init();
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new ToggleImpl(registry, "ZM", ON, OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        mainZoneToggle.init();
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        selectNet = new SelectImpl<>(registry, "NS", ExtendedSettings.values(), false);
        selectNet.setName("Network USB/AUDIO/IPOD Extended Control");
        selectNet.init();
        controls.add(selectNet);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }
}
