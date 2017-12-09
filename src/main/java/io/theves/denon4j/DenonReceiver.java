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

package io.theves.denon4j;

import io.theves.denon4j.controls.*;
import io.theves.denon4j.net.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class DenonReceiver implements AutoCloseable {
    private EventDispatcher eventDispatcher;
    private Protocol protocol;
    private Collection<Control> controls;
    private Toggle powerToggle;
    private Slider masterSlider;
    private Toggle mainZoneToggle;
    private Toggle muteToggle;
    private Select<InputSource> selectInput;
    private Select<VideoSource> selectVideo;
    private NetUsbIPod netUsb;
    private Menu menu;
    private SelectImpl<SurroundMode> selectSurround;

    /**
     * Starts auto discovery and chooses first receiver found.
     * Takes some time from time to time, so pls be patient.
     */
    public DenonReceiver(String subnet) {
        this(autoDiscover(subnet).getHostAddress(), 23);
    }

    private static InetAddress autoDiscover(String subnet) {
        AutoDiscovery autoDiscovery = new AutoDiscovery();
        autoDiscovery.setSubnet(subnet);
        Collection<InetAddress> discovered = autoDiscovery.discover(1);
        if (discovered.isEmpty()) {
            throw new ConnectionException("No receivers found");
        }
        return discovered.iterator().next();
    }

    public DenonReceiver(String host, int port) {
        this(new Tcp(host, port));
    }

    public DenonReceiver(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol);
        this.eventDispatcher = new EventDispatcher();
        this.controls = new ArrayList<>();

        this.protocol.setDispatcher(eventDispatcher);

        createControls(this.controls);
        addToDispatcher(this.controls);
    }

    private void createControls(Collection<? super Control> controls) {
        // power control
        powerToggle = new ToggleImpl(this, "PW", SwitchState.ON, SwitchState.STANDBY);
        powerToggle.setName("Power Switch");
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new SliderImpl(this, "MV", "UP", "DOWN");
        masterSlider.setName("Master Volume");
        controls.add(masterSlider);

        // mute control
        muteToggle = new ToggleImpl(this, "MU", SwitchState.ON, SwitchState.OFF);
        muteToggle.setName("Mute Toggle");
        controls.add(muteToggle);

        // select input
        selectInput = new SelectImpl<>(this, "SI", InputSource.values());
        selectInput.setName("Select INPUT Source");
        controls.add(selectInput);

        // select video
        selectVideo = new SelectImpl<>(this, "SV", VideoSource.values());
        selectVideo.setName("Select VIDEO Source");
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new ToggleImpl(this, "ZM", SwitchState.ON, SwitchState.OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        netUsb = new NetUsbIPod(this);
        controls.add(netUsb);

        menu = new Menu(this);
        controls.add(menu);

        selectSurround = new SelectImpl<>(this, "MS", SurroundMode.values());
        controls.add(selectSurround);
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.forEach(eventDispatcher::addListener);
    }

    public Select<SurroundMode> surroundMode() {
        return selectSurround;
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

    public NetUsbIPod netUsb() {
        return netUsb;
    }

    public Select<VideoSource> video() {
        return selectVideo;
    }

    public Menu menu() {
        return menu;
    }

    public void send(String command) {
        protocol.send(Command.createCommand(command));
    }

    public Collection<Control> getControls() {
        return controls;
    }

    @Override
    public void close() {
        disconnect();
    }

    public void disconnect() {
        getControls().forEach(eventDispatcher::removeListener);
        protocol.disconnect();
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }

    public Collection<EventListener> getEventListeners() {
        return eventDispatcher.getEventListeners();
    }

    EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
