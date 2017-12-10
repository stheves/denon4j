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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.TimeoutException;

import java.util.Objects;

/**
 * Base class for controls that handles requests/responses.
 *
 * @author stheves
 */
public abstract class AbstractControl implements Control {
    public static final long READ_TIMEOUT = 1000;

    private final String commandPrefix;
    private final DenonReceiver receiver;
    private final Object receiveLock = new Object();

    private Event mostRecent;
    private String name;
    private boolean receiving = false;

    public AbstractControl(DenonReceiver receiver, String commandPrefix) {
        this.commandPrefix = Objects.requireNonNull(commandPrefix);
        this.receiver = receiver;
    }

    protected void send(String param) {
        receiver.send(commandPrefix + param);
    }

    protected Event sendRequest() {
        synchronized (receiveLock) {
            try {
                receiving = true;
                receiver.send(commandPrefix + "?");
                try {
                    receiveLock.wait(READ_TIMEOUT);
                } catch (InterruptedException e) {
                    // ignore
                }
                if (mostRecent == null) {
                    throw new TimeoutException("No response received within " + READ_TIMEOUT);
                }
                return mostRecent;
            } finally {
                receiving = false;
            }
        }
    }

    @Override
    public final void handle(Event event) {
        if (receiving) {
            synchronized (receiveLock) {
                mostRecent = event;
                receiveLock.notify();
            }
        }
        doHandle(event);
    }

    protected abstract void doHandle(Event event);

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
