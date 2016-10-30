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

import de.theves.denon4j.model.Response;
import de.theves.denon4j.net.ConnectionException;
import de.theves.denon4j.net.NetClient;
import de.theves.denon4j.net.TcpClient;

import java.util.Optional;

/**
 * Abstract base class that implements the basic network protocol
 * for working with Denon receivers.
 *
 * @author Sascha Theves
 */
public abstract class AbstractAvReceiver {

    protected NetClient client;

    public AbstractAvReceiver(String hostname, Integer port) {
        this.client = new TcpClient(hostname, port);
    }

    /**
     * Creates a receiver with the given net client. The client is used for the communication with the receiver.
     *
     * @param client the client to use.
     */
    public AbstractAvReceiver(NetClient client) {
        this.client = client;
    }

    protected Optional<Response> send(String command)
            throws ConnectionException {
        return send(command, Optional.empty());
    }

    protected Optional<Response> send(String command, Optional<String> value)
            throws ConnectionException {
        return client.sendAndReceive(command, value);
    }

    public void connect(int timeout) throws ConnectionException {
        client.connect(timeout);
    }

    public void disconnect() throws ConnectionException {
        client.disconnect();
    }

    public boolean isConnected() throws ConnectionException {
        return client.isConnected();
    }
}