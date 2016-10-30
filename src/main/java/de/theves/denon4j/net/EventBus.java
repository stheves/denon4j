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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static de.theves.denon4j.net.NetClient.ENCODING;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class EventBus {
    private final InputStream inputStream;
    private Thread observerThread;
    private String lastEvent;
    private ReentrantLock lock = new ReentrantLock();

    public EventBus(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        listen();
    }

    private void listen() throws ConnectException {
        observerThread = new Thread(() -> {
            while (!observerThread.isInterrupted()) {
                doListen();
            }
        });
        observerThread.start();
    }

    public void interrupt() {
        observerThread.interrupt();
    }

    private void doListen() {
        try {
            waitForMessage();
        } catch (IOException e) {
            throw new ConnectException("Could not read from socket.", e);
        }
    }

    private void waitForMessage() throws IOException {
        lock.lock();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
            lastEvent = reader.readLine();
        } finally {
            lock.unlock();
        }
    }

    public Optional<String> get() {
        try {
            lock.tryLock(200, TimeUnit.MILLISECONDS);
            return Optional.ofNullable(lastEvent);
        } catch (InterruptedException e) {
            // no response within timeout
            return Optional.empty();
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {

            }
        }
    }
}
