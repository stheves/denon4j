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

package de.theves.denon4j.net;

import de.theves.denon4j.StateChangeListener;
import de.theves.denon4j.model.Command;
import de.theves.denon4j.model.Event;
import de.theves.denon4j.model.ReceiverState;
import de.theves.denon4j.model.Response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Sends the actual bytes of a command to the receiver.
 *
 * @author Sascha Theves
 */
public final class TcpClient implements NetClient {
    char END = 0x0d; // \r character
    Charset ENCODING = Charset.forName("US-ASCII");
    private final Integer port;
    private final String host;
    private Socket socket;
    private EventReceiver eventReceiver;
    private BufferedWriter writer;

    public TcpClient(String host, Integer port, Optional<EventReceiver> receiver) {
        this.host = Optional.ofNullable(host).orElse("192.168.1.105");
        this.port = Optional.ofNullable(port).orElse(23);
        if (receiver.isPresent()) {
            this.eventReceiver = receiver.get();
        }
    }

    @Override
    public synchronized void connect(int timeout) throws ConnectException {
        if (isConnected()) {
            throw new ConnectException("Already connected.");
        }
        try {
            socket = new Socket();
            socket.setSoTimeout(0);
            socket.connect(new InetSocketAddress(host, port), timeout);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            eventReceiver = new PollingEventReceiver(socket);
            // TODO add consumer in controller
            eventReceiver.addConsumer(new StateChangeListener(new ReceiverState()));
            eventReceiver.startListening();
        } catch (SocketTimeoutException ste) {
            throw new TimeoutException("Could not establish connection within timeout of " + timeout + " ms.", ste);
        } catch (IOException e) {
            throw new ConnectException("Cannot connect to host/ip " + host + " on port " + port, e);
        }
    }

    private void checkConnection() {
        if (!isConnected()) {
            throw new ConnectionException("Not connected.");
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
            eventReceiver.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
        } finally {
            socket = null;
            eventReceiver = null;
            writer = null;
        }
    }


    @Override
    public Optional<Response> send(Command command) {
        checkConnection();
        try {
            sendCommand(command.getCommand(), command.getParamter());
            return receiveResponse();
        } catch (Exception e) {
            throw new ConnectionException("Communication failure.", e);
        }
    }

    private void sendCommand(String command, Optional<String> parameter) throws IOException {
        String request = buildRequest(command, parameter);
        writer.write(request);
        writer.flush();
    }

    private Optional<Response> receiveResponse() {
        List<Event> events = new ArrayList<>();
        Optional<String> nextEvent;
        while ((nextEvent = eventReceiver.nextEvent(200)).isPresent()) {
            // TODO split event into command or prefix/parameter/value
            events.add(new Event(nextEvent.get()));
        }
        if (events.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Response(events));
        }
    }

    private String buildRequest(String command, Optional<String> value) {
        String request = command;
        if (value.isPresent()) {
            request += value.get();
        }
        return request + END;
    }
}
