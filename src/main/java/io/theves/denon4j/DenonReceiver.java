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

import static io.theves.denon4j.Condition.*;
import static java.lang.String.format;
import static java.time.Duration.ofMillis;

/**
 * Implementation of the Denon AVR 1912 protocol spec.
 *
 * @author stheves
 */
public class DenonReceiver implements AutoCloseable, EventDispatcher {
    private static final long RECV_TIMEOUT = 5 * 1000L;
    private static final int RETRIES = 3;
    private static final long WAIT_TIMEOUT = 250L;

    private final Logger log = Logger.getLogger(DenonReceiver.class.getName());
    private final Object sendReceiveLock = new Object();
    private final List<EventListener> eventListeners;
    private final Protocol protocol;

    private Collection<AbstractControl> controls;
    private Toggle powerToggle;
    private Volume masterSlider;
    private Toggle mainZoneToggle;
    private Toggle muteToggle;
    private Setting<InputSource> selectInput;
    private Setting<VideoSource> selectVideo;
    private NetUsbIPodControl netUsb;
    private Menu menu;
    private Setting<SurroundMode> selectSurround;
    private Session session;
    private Condition condition = bool(true);
    private RecvContext currentContext = new RecvContext();
    private SleepTimer sleepTimer;

    /**
     * Starts auto discovery and chooses first receiver found.
     * Takes some time from time to time, so pls be patient.
     */
    public DenonReceiver(String subnet) {
        this(autoDiscover(subnet).getHostAddress(), 23);
    }

    public DenonReceiver(String host, int port) {
        this(Protocol.tcp(host, port));
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

    private void createControls(Collection<AbstractControl> controls) {
        // power control
        powerToggle = new Toggle(this, "PW", SwitchState.ON, SwitchState.STANDBY);
        powerToggle.setName("Power Switch");
        controls.add(powerToggle);

        // master vol. control
        masterSlider = new Volume(this, "MV", "UP", "DOWN");
        masterSlider.setName("Master Volume");
        controls.add(masterSlider);

        // mute control
        muteToggle = new Toggle(this, "MU", SwitchState.ON, SwitchState.OFF);
        muteToggle.setName("Mute Toggle");
        controls.add(muteToggle);

        // select input
        selectInput = new Setting<>(this, "SI");
        selectInput.setName("Select INPUT Source");
        controls.add(selectInput);

        // select video
        selectVideo = new Setting<>(this, "SV");
        selectVideo.setName("Select VIDEO Source");
        controls.add(selectVideo);

        // main zone toggle
        mainZoneToggle = new Toggle(this, "ZM", SwitchState.ON, SwitchState.OFF);
        mainZoneToggle.setName("Main Zone Toggle");
        controls.add(mainZoneToggle);

        // network audio/usb/ipod DIRECT extended control
        netUsb = new NetUsbIPodControl(this, true);
        controls.add(netUsb);

        // main menu
        menu = new Menu(this);
        controls.add(menu);

        // surround mode settings
        selectSurround = new Setting<>(this, "MS");
        selectSurround.setName("Select Surround Mode");
        controls.add(selectSurround);

        sleepTimer = new SleepTimer(this);
        sleepTimer.setName("Main Zone Sleep Timer setting");
        controls.add(sleepTimer);
    }

    public SleepTimer sleepTimer() {
        return sleepTimer;
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

    private void addToDispatcher(Collection<AbstractControl> controls) {
        controls.forEach(this::addListener);
    }

    public Setting<SurroundMode> surroundMode() {
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

    public Setting<InputSource> input() {
        return selectInput;
    }

    public NetUsbIPodControl netUsb() {
        return netUsb;
    }

    public Setting<VideoSource> video() {
        return selectVideo;
    }

    public Menu menu() {
        return menu;
    }

    public void send(String command) {
        protocol.send(Command.createCommand(command));
    }


    /**
     * Send the command to the receiver and waits for the response until the <code>condition</code> is fulfilled.
     *
     * @param command the command to send.
     * @param c       the condition.
     * @return the received response.
     */
    public final List<Event> sendAndReceive(String command, Condition c) {
        if (command == null || c == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        synchronized (sendReceiveLock) {
            checkResponse(currentContext);
            // check for given condition but return after RETRIES or RECV_TIMEOUT to make sure this method returns
            condition = anyMatch(c, retries(RETRIES), duration(ofMillis(RECV_TIMEOUT)));
            try {
                currentContext.beginReceive();
                do {
                    send(command);
                    // wait for response
                    try {
                        sendReceiveLock.wait(WAIT_TIMEOUT);
                    } catch (InterruptedException e) {
                        log.log(Level.FINEST, "Got interruption while waiting for response", e);
                    }
                    currentContext.incrementCounter();
                } while (!condition.fulfilled(currentContext));

                // copy result
                return new ArrayList<>(currentContext.received());
            } finally {
                currentContext.endReceive();
            }
        }
    }

    private void checkResponse(RecvContext ctx) {
        if (!ctx.received().isEmpty()) {
            log.log(Level.WARNING, "Attempt to send new request on unfinished response.");
            // just to make sure we did not left anything in there...
            ctx.received().clear();
        }
    }

    @Override
    public final void dispatch(Event event) {
        recv(event);
        notifyEventListeners(event);
    }

    private void recv(Event event) {
        synchronized (sendReceiveLock) {
            if (currentContext.isReceiving()) {
                currentContext.received().add(event);
                if (condition.fulfilled(currentContext)) {
                    sendReceiveLock.notify();
                }
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

    public Collection<AbstractControl> getControls() {
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

    List<EventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }

    public Event sendRequest(String command, String regex) {
        return sendAndReceive(command, Condition.regex(regex)).stream().findFirst().orElseThrow(() -> new TimeoutException(
            format("No response received after %s milliseconds. Receiver may be too busy to respond.", RECV_TIMEOUT)
        ));
    }
}
