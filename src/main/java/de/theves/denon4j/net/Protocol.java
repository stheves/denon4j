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

/**
 * Low-level network client for communication with AVR.
 *
 * @author stheves
 */
public interface Protocol {
    /**
     * Sends the command to the receiver (socket) and waits for a response
     * (blocking).
     *
     * @param command the command to send (not <code>null</code>).
     * @throws ConnectionException if a communication failure occurs.
     */
    void send(Command command);

    /**
     * Sets the event listener for this protocol.
     * The event listener is registered to the event bus for receiving all events of the AVR.
     *
     * @param eventListener the event listener to use.
     */
    void setListener(EventListener eventListener);

    /**
     * Sends a request command.
     * A request command is a special command that has a immediate response in form of an {@link Event}.
     *
     * @param requestCommand the command to send.
     * @return the response of the command.
     */
    Event request(RequestCommand requestCommand);

    /**
     * Connect to the receiver.
     *
     * @param timeout the timeout in seconds to wait for connection establishment.
     * @throws ConnectionException if connection could not be established.
     */
    void establishConnection(int timeout) throws ConnectionException;

    /**
     * Disconnects this client.
     */
    void disconnect();

    /**
     * Returns <code>true</code> if this client is connected to the receiver.
     *
     * @return <code>true</code> if connected.
     */
    boolean isConnected();
}
