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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static de.theves.denon4j.net.NetClient.ENCODING;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class EventBus {
    private final BufferedReader reader;
    private final Logger logger = LoggerFactory.getLogger(EventBus.class);
    private Thread observerThread;
    private String lastEvent;
    private ReentrantLock lock = new ReentrantLock();

    public EventBus(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
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
        lock.lock();
        try {
            lastEvent = reader.readLine();
        } catch (SocketException se) {
            // siltently ignore
            logger.debug("Failure while reading from socker (this is expected if the socket got closed).", se);
        } catch (IOException e) {
            throw new ConnectException("Could not read from socket.", e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<String> get() {
        try {
            lock.tryLock(200, TimeUnit.MILLISECONDS);
            return Optional.ofNullable(lastEvent);
        } catch (InterruptedException e) {
            throw new ConnectionException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
