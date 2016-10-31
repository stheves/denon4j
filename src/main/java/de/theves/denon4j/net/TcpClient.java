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

import de.theves.denon4j.model.Command;
import de.theves.denon4j.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Objects;
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

    public TcpClient(String host, Integer port) {
        this.host = Objects.requireNonNull(host);
        this.port = Objects.requireNonNull(port);
    }

    @Override
    public void connect(int timeout) throws ConnectException {
        if (isConnected()) {
            throw new ConnectException("Already connected.");
        }
        try {
            socket = new Socket();
            socket.setSoTimeout(0);
            socket.connect(new InetSocketAddress(host, port), timeout);
            eventReceiver = new EventReceiver(socket);
            eventReceiver.start();
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
    public void disconnect() {
        try {
            eventReceiver.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
        } finally {
            socket = null;
            eventReceiver = null;
        }
    }


    @Override
    public Optional<Response> sendAndReceive(Command command, Optional<String> parameter) {
        checkConnection();
        try {
            sendCommand(command.getCommand(), parameter, socket.getOutputStream());
            return receiveResponse();
        } catch (Exception e) {
            throw new ConnectionException("Communication failure.", e);
        }
    }

    private void sendCommand(String command, Optional<String> value, OutputStream out) throws IOException {
        String request = buildRequest(command, value);
        out.write(request.getBytes(ENCODING));
        out.flush();
    }

    private Optional<Response> receiveResponse() throws IOException {
        // TODO receive the complete response and not only the first line
        Optional<String> nextEvent = eventReceiver.nextEvent();
        if (nextEvent.isPresent()) {
            return Optional.of(new Response(Collections.singletonList(nextEvent.get())));
        } else {
            return Optional.empty();
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
