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

package denon4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AvrMock implements Runnable {
    private Thread thread;
    private String commandString;
    private int port;

    public AvrMock(int port) {
        this.port = port;
    }


    public void start() throws IOException {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if (null != thread) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!thread.isInterrupted()) {
            Socket client;
            try {
                client = socket.accept();
                try {
                    Scanner commandScanner = new Scanner(client.getInputStream()).useDelimiter("\\r");
                    PrintWriter writer = new PrintWriter(client.getOutputStream());
                    while (commandScanner.hasNext()) {
                        String command = commandScanner.next();
                        if (null != command) {
                            this.commandString = command;
                        }
                        writer.println("Ok");
                        writer.flush();
                    }

                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to accept connections.", e);
            }
        }
    }

    public String getCommandString() {
        return commandString;
    }
}
