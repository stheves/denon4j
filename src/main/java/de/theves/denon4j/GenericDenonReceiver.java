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
import de.theves.denon4j.net.TimeoutException;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable generic receiver class that acts as the base class for all receiver implementations.
 * Offers the basic connection handling as well as a generic method to send commands to the receiver.
 * <p>
 * This class is intended to be subclassed by clients who want to implement their own receiver model.
 *
 * @author Sascha Theves
 * @see Avr1912
 */
public class GenericDenonReceiver {

    protected final NetClient client;
    protected final ResponseParser responseParser;

    /**
     * Creates a receiver for the given host and port.
     *
     * @param hostname the hostname or ip address of the avr receiver (e.g. 192.168.1.105)
     * @param port     the port of the receiver (standard is 23).
     */
    public GenericDenonReceiver(String hostname, Integer port) {
        this(new TcpClient(hostname, port));
    }

    /**
     * Creates a receiver with the given net client. The client is used for the communication with the receiver.
     *
     * @param client the client to use.
     */
    public GenericDenonReceiver(NetClient client) {
        this.client = Objects.requireNonNull(client);
        this.responseParser = new ResponseParser();
    }

    /**
     * Connects the client to the receiver. Call this method first before executing any commands.
     *
     * @param timeout the time to wait for connection establishment.
     * @throws TimeoutException                       if the connection timeout is reached.
     * @throws de.theves.denon4j.net.ConnectException if the client is already connected.
     * @throws ConnectionException                    if an error occurs connecting to the receiver.
     */
    public void connect(int timeout) throws ConnectionException {
        client.connect(timeout);
    }

    /**
     * Disconnect from receiver. Make sure to call this method when finished otherwise the connection relies open.
     *
     * @throws ConnectionException if an error occurs when disconnecting.
     */
    public void disconnect() throws ConnectionException {
        client.disconnect();
    }

    /**
     * Returns <code>true</code> if the client is connected to the receiver.
     *
     * @return <code>true</code> if connected.
     * @throws ConnectionException if the connection state cannot be determined.
     */
    public boolean isConnected() throws ConnectionException {
        return client.isConnected();
    }

    /**
     * Sends the <code>command</code> with <code>parameter</code> and <code>paramter</code> to the receiver.
     * Waits <code>readTimeout</code> ms for receiving the response.
     *
     * @param command the command (not <code>null</code>).
     * @return the plain response..
     */
    public Optional<Response> send(Command command) {
        return client.sendAndReceive(command);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // seems that somebody has forgotton to his/her housekeeping.
        // try to clean up now.
        if (client.isConnected()) {
            // try to clean up if not already done
            client.disconnect();
        }

    }
}
