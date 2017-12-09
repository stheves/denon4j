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

package io.theves.denon4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command line interface for Denon audio/video receivers.
 *
 * @author stheves
 */
public class Cli {
    private List<String> args;

    public Cli(String[] args) {
        this.args = new ArrayList<>();
        this.args.addAll(Arrays.asList(args));
    }

    public static void main(String[] args) {
        if (null == args || args.length != 2) {
            System.err.println("Try java -jar $/path/to/jar $host $port");
            quit(1);
        }
        Cli cli = new Cli(args);
        cli.interactiveMode();
        quit(0);
    }

    private static void quit(int status) {
        System.exit(status);
    }

    public void interactiveMode() {
        try (DenonReceiver avr = new DenonReceiver(args.get(0), Integer.parseInt(args.get(1)))) {
            avr.connect(1000);
            System.out.println("Enter a command ('?' for help, 'q' for quit):");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String input = readInput(reader);
                    if (input != null) {
                        if ("?".equals(input)) {
                            printHelp();
                            continue;
                        }
                        if ("q".equals(input)) {
                            break;
                        }
                        if (input.length() > 2) {
                            avr.send(input);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("...bye");
        }
    }

    private String readInput(BufferedReader reader) throws IOException {
        System.out.print("> ");
        return reader.readLine();
    }

    private void printHelp() {
        PrintWriter writer = new PrintWriter(System.out);
        writer.println();
        writer.println("The following options are available:");
        writer.println();
        writer.println("?\t\t\tPrints this help");
        writer.println("q\t\t\tQuits the program");
        writer.println("<cmd>\t\tExecutes a generic command e.g. PW?");
        writer.println("PW?\t\t\tReturns the power state");
        writer.println("PWON\t\tTurns power on");
        writer.println();
        writer.println("See the Denon AVR protocol for a full list of commands.");
        writer.flush();
    }
}
