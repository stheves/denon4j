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

package de.theves.denon4j.internal.net;

import de.theves.denon4j.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Sends the actual bytes of a command to the receiver.
 *
 * @author stheves
 */
public final class Tcp implements Protocol {
    private final static char CR = 0x0d; // \r character

    private final Logger logger = LoggerFactory.getLogger(Tcp.class);
    private final Integer port;
    private final String host;
    private final EventReader eventReader;

    private EventListener eventListener;
    private Socket socket;
    private Writer writer;
    private Event mostRecent;

    public Tcp(String host, Integer port) {
        this.host = Optional.ofNullable(host).orElse("127.0.0.1");
        this.port = Optional.ofNullable(port).orElse(23);
        socket = new Socket();
        eventReader = new EventReader(this, socket);
    }

    @Override
    public synchronized void establishConnection(int timeout) throws ConnectException {
        if (isConnected()) {
            throw new ConnectException("Already connected.");
        }
        try {
            socket.setSoTimeout(0); // set infinite poll timeout
            socket.connect(new InetSocketAddress(host, port), timeout);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

            eventReader.start();
        } catch (SocketTimeoutException ste) {
            throw new TimeoutException("Could not establish connection within timeout of " + timeout + " ms.", ste);
        } catch (IOException e) {
            throw new ConnectException("Cannot establishConnection to host/ip " + host + " on port " + port, e);
        }
    }

    @Override
    public boolean isConnected() {
        return null != socket && socket.isConnected();
    }

    @Override
    public synchronized void disconnect() {
        if (!isConnected()) {
            return;
        }
        try {
            eventReader.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
            logger.debug("Disconnect failure", e);
        }
    }

    @Override
    public void send(Command command) {
        checkConnection();
        doSend(command);
    }

    @Override
    public void setListener(EventListener participant) {
        this.eventListener = participant;
    }

    @Override
    public Event receive(RequestCommand requestCommand) {
        synchronized (eventReader) {
            send(requestCommand);
            // wait until response is received
            try {
                eventReader.wait(200);
            } catch (InterruptedException e) {
                logger.warn("Receive interrupted", e);
            }
        }
        // we expecting the last event as result of the request command
        return mostRecent;
    }

    void received(String event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Event received: {}", event);
        }
        mostRecent = EventImpl.create(event);
        notify(mostRecent);
    }

    private void notify(Event event) {
        if (null != eventListener) {
            try {
                eventListener.onEvent(event);
            } catch (Exception e) {
                logger.warn("Error invoking event listener", e);
            }
        }
    }

    private void checkConnection() {
        if (!isConnected()) {
            throw new ConnectionException("Not connected.");
        }
    }

    private void doSend(Command command) {
        try {
            writer.write(command.build() + CR);
            writer.flush();
        } catch (Exception e) {
            throw new ConnectionException("Communication failure.", e);
        }
    }
}
