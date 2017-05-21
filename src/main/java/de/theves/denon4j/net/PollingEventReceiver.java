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
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sascha Theves
 */
public class PollingEventReceiver extends Thread implements Runnable {

    private static final AtomicInteger THREAD_NUM = new AtomicInteger(0);

    private final Logger logger = LoggerFactory.getLogger(PollingEventReceiver.class);

    private final BlockingQueue<String> eventQueue;

    private final Socket socket;
    private final TcpClient client;

    private BufferedReader reader;

    public PollingEventReceiver(TcpClient client, Socket socket) {
        super("PollingEventReceiver-" + THREAD_NUM.getAndAdd(1));
        this.client = client;
        this.socket = socket;
        this.eventQueue = new ArrayBlockingQueue<>(256);
        openStream();
    }

    public void startListening() {
        start();
    }

    public void run() {
        logger.debug("PollingEventReceiver started. Listening for events...");
        while (!isInterrupted()) {
            poll();
        }
        logger.debug("PollingEventReceiver stopped.");
    }

    private void openStream() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    private void poll() {
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
}
