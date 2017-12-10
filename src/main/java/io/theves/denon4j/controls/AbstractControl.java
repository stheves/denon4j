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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.TimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Base class for controls that handles requests/responses.
 *
 * @author stheves
 */
public abstract class AbstractControl implements Control {
    private static final long READ_TIMEOUT = 220; // as of spec 200ms + 20ms delay

    private final String commandPrefix;
    private final DenonReceiver receiver;
    private final Object sendReceiveLock = new Object();

    private List<Event> response = new ArrayList<>();
    private String name;
    private boolean receiving = false;
    private CompletionCallback callback;

    public AbstractControl(DenonReceiver receiver, String commandPrefix) {
        this.commandPrefix = Objects.requireNonNull(commandPrefix);
        this.receiver = receiver;
    }

    protected final void send(String param) {
        receiver.send(commandPrefix + param);
    }

    final Event sendRequest() {
        List<Event> response = new ArrayList<>();
        int retries = 0;
        while (response.isEmpty() && retries < 3) {
            // do retry - receiver is maybe too busy to answer
            response = doSendRequest();
            retries++;

        }
        if (response.isEmpty()) {
            throw new TimeoutException(
                format("No response received after %d retries. Maybe receiver is too busy answer.", retries)
            );
        }
        return response.get(0);
    }

    private List<Event> doSendRequest() {
        return sendAndReceive("?",
            response -> response.size() == 1 && response.get(0).startsWith(getCommandPrefix())
        );
    }

    final List<Event> sendAndReceive(String param, CompletionCallback completionCallback) {
        // obtain lock to safe state
        synchronized (sendReceiveLock) {
            try {
                receiving = true;
                callback = completionCallback;
                response.clear();
                send(param);
                waitForResponse();
                return new ArrayList<>(this.response);
            } finally {
                receiving = false;
                response.clear();
                callback = null;
            }
        }
    }

    private void waitForResponse() {
        try {
            sendReceiveLock.wait(READ_TIMEOUT);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public final void handle(Event event) {
        if (shouldHandle(event)) {
            synchronized (sendReceiveLock) {
                if (receiving) {
                    response.add(event);
                }
                doHandle(event);
                if (isComplete()) {
                    sendReceiveLock.notify();
                }
            }
        }
    }

    private boolean shouldHandle(Event event) {
        return event.startsWith(getCommandPrefix());
    }

    private boolean isComplete() {
        return callback == null || callback.isComplete(this.response);
    }

    protected void doHandle(Event event) {
        // subclasses may override
    }

    @Override
    public String getCommandPrefix() {
        return commandPrefix;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Control{" +
            "commandPrefix='" + commandPrefix + '\'' +
            ", name='" + name + '\'' +
            '}';
    }


}
