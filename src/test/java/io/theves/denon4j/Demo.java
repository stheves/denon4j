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

//            Slider masterVolume = avr.masterVolume();
//            masterVolume.slideUp();
//            System.out.println("MASTER VOL: " + masterVolume.getValue());
//            masterVolume.set("25");
//            System.out.println("MASTER VOL: " + masterVolume.getValue());
//
//            avr.video().select(VideoSource.SOURCE);
//            avr.input().select(InputSource.NET_UBS);
//            avr.netUsb().play();

            System.out.println("Display: ");
            System.out.println(avr.netUsb().getDisplay());


//            avr.menu().control(MenuControls.MENU_ON);
//            avr.menu().control(MenuControls.CURSOR_DOWN);
//            avr.menu().control(MenuControls.MENU_OFF);

            System.out.println("Surround Mode: " + avr.surroundMode().get());

            avr.getSession().stats().print(System.out);
        }
        System.out.println("------------DEMO END------------");
    }
}
