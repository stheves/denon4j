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

import de.theves.denon4j.model.Command;
import de.theves.denon4j.model.Response;
import de.theves.denon4j.net.ConnectionException;
import de.theves.denon4j.net.NetClient;
import de.theves.denon4j.net.TcpClient;

import java.util.Optional;

/**
 * Generic receiver class.
 *
 * @author Sascha Theves
 */
public class GenericDenonReceiver {

    protected NetClient client;

    public GenericDenonReceiver(String hostname, Integer port) {
        this.client = new TcpClient(hostname, port);
    }

    /**
     * Creates a receiver with the given net client. The client is used for the communication with the receiver.
     *
     * @param client the client to use.
     */
    public GenericDenonReceiver(NetClient client) {
        this.client = client;
    }

    protected Optional<Response> send(Command command)
            throws ConnectionException {
        return send(command, Optional.empty());
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

    /**
     * Sends the <code>command</code> with <code>parameter</code> and <code>paramter</code> to the receiver.
     * Waits <code>readTimeout</code> ms for receiving the response.
     *
     * @param command  the command (not <code>null</code>).
     * @param paramter the paramter to send (may be <code>empty</code>).
     * @return the plain response..
     */
    public Optional<Response> send(Command command, Optional<String> paramter) {
        return client.sendAndReceive(command, paramter);
    }
}
