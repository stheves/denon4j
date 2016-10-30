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
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static de.theves.denon4j.net.NetClient.ENCODING;

/**
 * @author Sascha Theves
 */
public class EventReceiver extends Thread {
    private final BufferedReader reader;
    private final Logger logger = LoggerFactory.getLogger(EventReceiver.class);
    private final BlockingQueue<String> eventQueue;
    private String lastEvent;

    public EventReceiver(Socket socket) throws IOException {
        super("EventReceiver");
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
        this.eventQueue = new ArrayBlockingQueue<>(128);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            listen();
        }
    }

    private void listen() {
        try {
            String lastEvent = reader.readLine();
            System.out.println(lastEvent);
            eventQueue.put(lastEvent);

        } catch (SocketException se) {
            // siltently ignore
            logger.debug("Failure while reading from socker (this is expected if the socket got closed).", se);
        } catch (IOException e) {
            throw new ConnectException("Could not read from socket.", e);
        } catch (InterruptedException e) {
            // TODO other ex here!?
            throw new RuntimeException("EventQueue communication failure.", e);
        }
    }

    public Optional<String> nextEvent() {
        try {
            // as of specification we should receive a response within 200ms
            return Optional.ofNullable(eventQueue.poll(200, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }
}
