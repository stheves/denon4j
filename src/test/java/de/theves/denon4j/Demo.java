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

package de.theves.denon4j;

import de.theves.denon4j.controls.*;

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

    private void demo(String host, int port) throws Exception {
        System.out.println("------------DEMO START------------");
        try (DenonReceiver avr = new DenonReceiver(host, port)) {
            // establish connection
            avr.connect(1000);

            // power control
            Toggle power = avr.power();
            System.out.println("PWON: " + power.state().get());
            if (power.state() != SwitchState.ON) {
                // powering on
                power.toggle();
            }

            Toggle mute = avr.mute();
            mute.toggle();
            System.out.println("MUTE ON: " + mute.state().get());

            Thread.sleep(200);
            mute.switchOff();
            System.out.println("MUTE OFF: " + mute.state().get());

            Slider masterVolume = avr.masterVolume();
            masterVolume.slideUp();
            System.out.println("MASTER VOL: " + masterVolume.getValue());
            masterVolume.set("25");
            System.out.println("MASTER VOL: " + masterVolume.getValue());

            Thread.sleep(200);
            avr.video().select(VideoSource.SOURCE);

            Thread.sleep(200);
            avr.input().select(InputSource.NET_UBS);

            Thread.sleep(200);
            avr.networkControl().select(NetworkControls.CURSOR_RIGHT);
            System.out.println("Display: " + avr.networkControl().getDisplay());

            Thread.sleep(200);
            avr.menu().control(MenuControls.MENU_ON);

            Thread.sleep(200);
            avr.menu().control(MenuControls.CURSOR_DOWN);

            Thread.sleep(200);
            avr.menu().control(MenuControls.MENU_OFF);

            Thread.sleep(200);
            System.out.println("Surround Mode: " + avr.surroundMode().get().getMode());
        }
        System.out.println("------------DEMO END------------");
    }
}
