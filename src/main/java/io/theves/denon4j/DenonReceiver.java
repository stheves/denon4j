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
    private ToggleImpl powerToggle;
    private SliderImpl masterSlider;
    private ToggleImpl mainZoneToggle;
    private ToggleImpl muteToggle;
    private SelectImpl<InputSource> selectInput;
    private SelectImpl<VideoSource> selectVideo;
    private NetworkControlImpl selectNet;
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
        powerToggle = new ToggleImpl(protocol, "PW", SwitchState.ON, SwitchState.STANDBY);
        powerToggle.setName("Power Switch");
        powerToggle.init();
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new SliderImpl(protocol, "MV", "UP", "DOWN");
        masterSlider.setName("Master Volume");
        masterSlider.init();
        controls.add(masterSlider);

        // mute control
        muteToggle = new ToggleImpl(protocol, "MU", SwitchState.ON, SwitchState.OFF);
        muteToggle.setName("Mute Toggle");
        muteToggle.init();
        controls.add(muteToggle);

        // select input
        selectInput = new SelectImpl<>(protocol, "SI", InputSource.values());
        selectInput.setName("Select INPUT Source");
        selectInput.init();
        controls.add(selectInput);

        // select video
        selectVideo = new SelectImpl<>(protocol, "SV", VideoSource.values());
        selectVideo.setName("Select VIDEO Source");
        selectVideo.init();
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new ToggleImpl(protocol, "ZM", SwitchState.ON, SwitchState.OFF);
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

        selectSurround = new SelectImpl<>(protocol, "MS", SurroundMode.values());
        selectSurround.init();
        controls.add(selectSurround);
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.forEach(eventDispatcher::addControl);
    }

    public Select<SurroundMode> surroundMode() {
        return selectSurround;
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

    public String send(String command) {
        checkConnected();
        Command cmd = Command.createCommand(protocol, command);
        cmd.execute();
        if (cmd instanceof RequestCommand) {
            return ((RequestCommand) cmd).getReceived().getParameter().getValue();
        }
        return null;
    }

    private void checkConnected() {
        if (!isConnected()) {
            throw new ConnectionException("Not connected");
        }
    }

    public Collection<Control> getControls() {
        return controls;
    }

    @Override
    public void close() {
        disconnect();
    }

    public void disconnect() {
        getControls().forEach(eventDispatcher::removeControl);
        getControls().forEach(Control::dispose);
        protocol.disconnect();
    }

    public void connect(int timeout) {
        protocol.establishConnection(timeout);
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }
}
