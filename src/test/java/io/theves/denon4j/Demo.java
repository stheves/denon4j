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

package io.theves.denon4j;

import io.theves.denon4j.controls.*;

public class Demo {

    public static void main(String[] args) throws Exception {
        if (null == args || args.length != 2) {
            System.err.println("Try java -jar $/path/to/jar $host $port");
            System.exit(1);
        }
        System.out.println(String.format("Starting demo... AVR1912: %s:%s",
            args[0], args[1]));
        Demo test = new Demo();
        test.demo(args[0], Integer.parseInt(args[1]));
    }

    private void demo(String host, int port) {
        System.out.println("------------DEMO START------------");
        try (DenonReceiver avr = new DenonReceiver(host, port)) {
            // establish connection
            avr.connect(1000);

            // power control
            Toggle power = avr.power();
            System.out.println("PWON: " + power.state());
            if (!power.state().equals("ON")) {
                // powering on
                power.toggle();
            }

            Volume masterVolume = avr.masterVolume();
            masterVolume.slideUp();
            System.out.println("MASTER VOL: " + masterVolume.get());
            masterVolume.set("55");
            System.out.println("MASTER VOL: " + masterVolume.get());
            System.out.println("MASTER VOL MAX: " + masterVolume.getMax());

            avr.video().set(VideoSource.SOURCE.getSource());
            avr.input().set(InputSource.NET_UBS.getInputSource());
            avr.netUsb().cursorRight();

            System.out.println("Display: ");
            System.out.println(avr.netUsb().getDisplay());

            avr.menu().control(MenuNavigation.MENU_ON);
            avr.menu().control(MenuNavigation.CURSOR_DOWN);
            avr.menu().control(MenuNavigation.MENU_OFF);

            System.out.println("SLEEP TIMER: " + avr.sleepTimer().getTimer());
            avr.sleepTimer().timer("027");
            System.out.println("SLEEP TIMER: " + avr.sleepTimer().getTimer());
            avr.sleepTimer().off();

            System.out.println("Surround Mode: " + avr.surroundMode().get());

            avr.getSession().stats().print(System.out);
        }
        System.out.println("------------DEMO END------------");
    }
}
