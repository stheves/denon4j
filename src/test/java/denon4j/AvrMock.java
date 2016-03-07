/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package denon4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AvrMock implements Runnable {
    private static final int RANDOM_PORT = 0;

    private Thread thread;
    private String commandString;
    private int port;
    private ServerSocket socket;

    public AvrMock() {
        // default constructor
    }


    public void start() throws IOException {
        this.socket = connect();
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
        listen();
    }

    private void listen() {
        if (null == this.socket || !this.socket.isBound()) {
            throw new IllegalStateException("Not connected, call start first.");
        }
        while (!thread.isInterrupted()) {
            Socket client;
            try {
                client = this.socket.accept();
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

                }
                finally {
                    try {
                        client.close();
                    }
                    catch (IOException e) {
                        // ignore
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to accept connections.", e);
            }
        }
    }

    private ServerSocket connect() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(RANDOM_PORT);
            this.port = socket.getLocalPort();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }

    public String getCommandString() {
        return commandString;
    }

    public int getPort() {
        return port;
    }
}
