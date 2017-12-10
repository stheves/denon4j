/*
 * Copyright 2017 Sascha Theves
 *
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

package io.theves.denon4j.net;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author stheves
 */
public class EventReader extends Thread {

    private final Logger logger = Logger.getLogger(EventReader.class.getName());

    private final Socket socket;
    private final Tcp client;

    private InputStream reader;

    EventReader(Tcp client, Socket socket) {
        super("EventReader");
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run() {
        openStream();
        logger.log(Level.FINE, "Listening for events...");
        while (!isInterrupted()) {
            next();
        }
        logger.log(Level.FINE, "Stopped.");
    }

    private void openStream() {
        try {
            this.reader = socket.getInputStream();
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    private void next() {
        try {
            int read;
            ByteBuffer rawBuffer = ByteBuffer.allocate(135);
            while ((read = reader.read()) != -1) {
                if (Protocol.PAUSE == read || Protocol.NULL == read) {
                    break;
                }
                rawBuffer.put((byte) read);
            }
            if (rawBuffer.position() > 0) {
                Event e = Event.create(Arrays.copyOfRange(rawBuffer.array(), 0, rawBuffer.position()));
                client.received(e);
            }
        } catch (SocketException se) {
            if (!socket.isClosed() && !socket.isInputShutdown()) {
                throw new ConnectionException("Socket error.", se);
            }
        } catch (Exception e) {
            throw new ConnectionException("Socket error.", e);
        }
    }
}
