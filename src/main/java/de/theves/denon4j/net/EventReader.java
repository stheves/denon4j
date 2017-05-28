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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Sascha Theves
 */
public class EventReader extends Thread implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(EventReader.class);

    private final BlockingQueue<String> eventQueue;

    private final Socket socket;
    private final Tcp client;

    private BufferedReader reader;

    public EventReader(Tcp client, Socket socket) {
        super("EventReader");
        this.client = client;
        this.socket = socket;
        this.eventQueue = new ArrayBlockingQueue<>(256);
        openStream();
    }

    @Override
    public void run() {
        logger.debug("Listening for events...");
        while (!isInterrupted()) {
            read();
        }
        logger.debug("Stopped.");
    }

    private void openStream() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    private void read() {
        try {
            String lastEvent = reader.readLine();
            if (logger.isDebugEnabled()) {
                logger.debug("Event received: " + lastEvent);
            }
            eventQueue.put(lastEvent);
            client.received(lastEvent);
        } catch (SocketException se) {
            if (!socket.isClosed() && !socket.isInputShutdown()) {
                throw new ConnectionException("Socket error.", se);
            }
        } catch (Exception e) {
            throw new ConnectionException("Socket error.", e);
        }
    }

    public String poll(long timeout) {
        String event = null;
        try {
            event = eventQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("Unexpected interrupt", e);
        }
        return event;
    }
}
