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
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class AutoDiscovery {
    private int start = 0;
    private int end = 254;
    private int step = 1;
    private String subnet = "127.0.0";
    private int port = 23;
    private int timeout = 55;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        if (start >= 0 && start < 255) {
            this.start = start;
        }
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        if (end > 0 && end < 255) {
            this.end = end;
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step > 0) {
            this.step = step;
        }
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        if (null != subnet) {
            this.subnet = subnet;
        }
    }

    public Collection<InetAddress> discover() {
        return discover(255);
    }

    public Collection<InetAddress> discover(int max) {
        if (start < 0 || end < 0 || start > 255) {
            throw new IllegalArgumentException("from/to must be in range 0-254");
        }
        Collection<InetAddress> discoveredServers = new ArrayList<>();
        for (int i = start; i <= end; i = i + step) {
            String host = subnet + "." + i;
            try {
                InetAddress address = InetAddress.getByName(host);
                timeout = 200;
                if (address.isReachable(timeout)) {
                    try (Socket s = new Socket(host, port)) {
                        s.close();
                        discoveredServers.add(address);
                        if (discoveredServers.size() == max) {
                            // stop immediate
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                // unreachable
            }
        }
        return discoveredServers;
    }

    public void setPort(int port) {
        if (port > 0) {
            this.port = port;
        }
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }
}
