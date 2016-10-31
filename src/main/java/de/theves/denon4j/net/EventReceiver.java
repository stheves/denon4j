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

import java.util.List;
import java.util.Optional;

/**
 * Receiver is the low level class for receiving events.
 *
 * @author Sascha Theves
 */
public interface EventReceiver extends Runnable {
    /**
     * Starts listening for events.
     * This method is non-blocking when waiting for events to come in.
     */
    void startListening();

    /**
     * Read the next available event from the even stream.
     * Blocks until an element arrives or the timeout is reached.
     *
     * @param timeout the read timeout in millis.
     * @return the next event or an empty result if the timeout has been reached.
     */
    Optional<String> nextEvent(int timeout);

    /**
     * Adds a consumer to the queue.
     *
     * @param consumer the consumer (not <code>null</code>).
     */
    void addConsumer(EventConsumer consumer);

    /**
     * Removes the consumer from the queue.
     *
     * @param consumer the consumer to remove (not <code>null</code>).
     */
    void removeConsumer(EventConsumer consumer);

    /**
     * Returns a unmodifiable list view of all consumers.
     *
     * @return the list of all consumers (not <code>null</code>).
     */
    List<EventConsumer> getEventConsumers();

    /**
     * Interrupts listening for events.
     */
    void interrupt();
}
