/*
 * Copyright 2017 Sascha Theves
 *
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
import io.theves.denon4j.net.EventListener;

import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class DenonReceiver implements AutoCloseable, EventDispatcher {
    private static final long READ_TIMEOUT = 220;

    private final Logger log = Logger.getLogger(DenonReceiver.class.getName());
    private final Object sendReceiveLock = new Object();
    private final List<EventListener> eventListeners;
    private final Protocol protocol;

    private Collection<Control> controls;
    private Toggle powerToggle;
    private Volume masterSlider;
    private Toggle mainZoneToggle;
    private Toggle muteToggle;
    private SelectImpl<InputSource> selectInput;
    private SelectImpl<VideoSource> selectVideo;
    private NetUsbIPod netUsb;
    private Menu menu;
    private SelectImpl<SurroundMode> selectSurround;
    private Session session;
    private boolean receiving = false;
    private CompletionCallback callback = null;
    private List<Event> response = new ArrayList<>();

    /**
     * Starts auto discovery and chooses first receiver found.
     * Takes some time from time to time, so pls be patient.
     */
    public DenonReceiver(String subnet) {
        this(autoDiscover(subnet).getHostAddress(), 23);
    }

    public DenonReceiver(String host, int port) {
        this(new Tcp(host, port));
    }

    public DenonReceiver(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol);
        this.eventListeners = Collections.synchronizedList(new ArrayList<>());
        this.controls = new ArrayList<>();
        this.protocol.setDispatcher(this);

        createControls(this.controls);
        addToDispatcher(this.controls);
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

    private void createControls(Collection<? super Control> controls) {
        // power control
        powerToggle = new ToggleImpl(this, "PW", SwitchState.ON, SwitchState.STANDBY);
        powerToggle.setName("Power Switch");
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new Volume(this, "MV", "UP", "DOWN");
        masterSlider.setName("Master Volume");
        controls.add(masterSlider);

        // mute control
        muteToggle = new ToggleImpl(this, "MU", SwitchState.ON, SwitchState.OFF);
        muteToggle.setName("Mute Toggle");
        controls.add(muteToggle);

        // select input
        selectInput = new SelectImpl<>(this, "SI");
        selectInput.setName("Select INPUT Source");
        controls.add(selectInput);

        // select video
        selectVideo = new SelectImpl<>(this, "SV");
        selectVideo.setName("Select VIDEO Source");
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new ToggleImpl(this, "ZM", SwitchState.ON, SwitchState.OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        netUsb = new NetUsbIPod(this, true);
        controls.add(netUsb);

        menu = new Menu(this);
        controls.add(menu);

        selectSurround = new SelectImpl<>(this, "MS");
        selectSurround.setName("Select Surround Mode");
        controls.add(selectSurround);
    }

    public void addListener(EventListener listener) {
        if (null != listener) {
            eventListeners.add(listener);
        }
    }

    public void removeListener(EventListener eventListener) {
        if (null != eventListener) {
            eventListeners.remove(eventListener);
        }
    }

    private void addToDispatcher(Collection<Control> controls) {
        controls.forEach(this::addListener);
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

    public Volume masterVolume() {
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

    public final Event sendRequest(String command, String regex) {
        List<Event> response;
        int retries = 0;
        do {
            // do retry - receiver is maybe too busy to answer
            response = doSendRequest(command, regex);
            retries++;
        } while (!isComplete() && retries < 3);

        if (response.isEmpty()) {
            throw new TimeoutException(
                format("No response received after %d retries. Maybe receiver is too busy answer.", retries)
            );
        }
        return response.get(0);
    }

    private List<Event> doSendRequest(String command, String regex) {
        return sendAndReceive(command,
            res -> res.stream().anyMatch(e -> e.asciiValue().matches(regex))
        );
    }

    public final List<Event> sendAndReceive(String command, CompletionCallback completionCallback) {
        // obtain lock to safe state
        synchronized (sendReceiveLock) {
            try {
                receiving = true;
                callback = completionCallback;
                response.clear();
                send(command);
                waitForResponse();
                return new ArrayList<>(this.response);
            } finally {
                receiving = false;
                response.clear();
                callback = null;
            }
        }
    }

    private void waitForResponse() {
        try {
            sendReceiveLock.wait(READ_TIMEOUT);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public final void dispatch(Event event) {
        synchronized (sendReceiveLock) {
            if (receiving) {
                response.add(event);
            }
            notifyEventListeners(event);
            if (isComplete()) {
                sendReceiveLock.notify();
            }
        }
    }

    private void notifyEventListeners(Event event) {
        synchronized (eventListeners) {
            eventListeners.forEach(listener -> {
                try {
                    listener.handle(event);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Caught exception from listener: " + listener, e);
                }
            });
        }
    }

    private boolean isComplete() {
        return callback != null && callback.isComplete(this.response);
    }

    public Collection<Control> getControls() {
        return controls;
    }

    @Override
    public void close() {
        disconnect();
    }

    public void disconnect() {
        getControls().forEach(this::removeListener);
        protocol.disconnect();
        session.finish();
    }

    public void connect(int timeout) {
        session = new Session(this);
        protocol.establishConnection(timeout);
    }

    public Session getSession() {
        return session;
    }

    public boolean isConnected() {
        return protocol.isConnected();
    }

    public List<EventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }
}
