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
public class AVR1912 implements AVR {
    private static final Pattern PATTERN_MASTER_VOL = Pattern.compile("[0-9][0-9][5]?");

    private final EventDispatcher eventDispatcher;
    private final Protocol protocol;
    private final Collection<Control> controls;
    private final CommandRegistry registry;

    private String powerTogglePrefix;
    private String masterSliderPrefix;
    private String muteTogglePrefix;
    private String mainZoneTogglePrefix;
    private String selectNetPrefix;
    private String selectVideoPrefix;
    private String selectInputPrefix;

    public AVR1912(String host, int port) {
        this(new Tcp(host, port));
    }

    AVR1912(Protocol protocol) {
        this.protocol = protocol;
        this.registry = new CommandRegistryImpl(protocol);
        this.eventDispatcher = new EventDispatcher(protocol);
        this.controls = new ArrayList<>();
        addControls(this.controls);
        addToDispatcher(this.controls);
    }

    private void addControls(Collection<? super Control> controls) {
        // power control
        powerTogglePrefix = "PW";
        Toggle powerToggle = new ToggleImpl(registry, powerTogglePrefix, ON, STANDBY);
        powerToggle.setName("Power Switch");
        powerToggle.init();
        controls.add(powerToggle);

        // master vol. control
        masterSliderPrefix = "MV";
        Slider masterSlider = new SliderImpl(registry, masterSliderPrefix, "UP", "DOWN", PATTERN_MASTER_VOL);
        masterSlider.setName("Master Volume");
        masterSlider.init();
        controls.add(masterSlider);

        // mute control
        muteTogglePrefix = "MU";
        Toggle muteToggle = new ToggleImpl(registry, muteTogglePrefix, ON, OFF);
        muteToggle.setName("Mute Toggle");
        muteToggle.init();
        controls.add(muteToggle);

        // source input
        selectInputPrefix = "SI";
        Select<InputSource> selectInput = new SelectImpl<>(registry, selectInputPrefix, InputSource.values(), true);
        selectInput.setName("Select INPUT Source");
        selectInput.init();
        controls.add(selectInput);

        // source video
        selectVideoPrefix = "SV";
        Select<VideoSource> selectVideo = new SelectImpl<>(registry, selectVideoPrefix, VideoSource.values(), true);
        selectVideo.setName("Select VIDEO Source");
        selectVideo.init();
        controls.add(selectVideo);

        // main zone toggle
        mainZoneTogglePrefix = "ZM";
        Toggle mainZoneToggle = new ToggleImpl(registry, mainZoneTogglePrefix, ON, OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        mainZoneToggle.init();
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        selectNetPrefix = "NS";
        Select<ExtendedSettings> selectNet = new SelectImpl<>(registry, selectNetPrefix, ExtendedSettings.values(), false);
        selectNet.setName("Network USB/AUDIO/IPOD Extended Control");
        selectNet.init();
        controls.add(selectNet);
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.stream().forEach(eventDispatcher::addControl);
    }

    EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public Toggle power() {
        return findControl(powerTogglePrefix, Toggle.class);
    }

    private <C extends Control> C findControl(String prefix, Class<C> cls) {
        return cls.cast(controls.stream()
                .filter(control -> control.getCommandPrefix().equals(prefix))
                .findFirst().orElseThrow(IllegalStateException::new));
    }

    public Toggle mainZone() {
        return findControl(mainZoneTogglePrefix, Toggle.class);
    }

    public Slider masterVolume() {
        return findControl(masterSliderPrefix, Slider.class);
    }

    public Toggle mute() {
        return findControl(muteTogglePrefix, Toggle.class);
    }

    public Select<InputSource> selectInput() {
        return findControl(selectInputPrefix, Select.class);
    }

    public Select<ExtendedSettings> selectNetworkControl() {
        return findControl(selectNetPrefix, Select.class);
    }

    public Select<VideoSource> selectVideo() {
        return findControl(selectVideoPrefix, Select.class);
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
        // first start dispatching to make sure we do not miss anything
        eventDispatcher.startDispatching();
        protocol.establishConnection(timeout);
    }

    public CommandRegistry getRegistry() {
        return registry;
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
