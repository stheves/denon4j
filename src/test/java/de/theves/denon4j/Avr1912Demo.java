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

import de.theves.denon4j.model.Playback;
import de.theves.denon4j.model.Sources;
import de.theves.denon4j.model.Volume;

public class Avr1912Demo {

    public void demo(String host, int port) throws Exception {
        Avr1912 avr = new Avr1912(host, port);
        avr.connect(1000);


        try {
            System.out.println("PWON: " + avr.isPowerOn());
            if (!avr.isPowerOn()) {
                System.out.println("POWERING ON: " + avr.powerOn());
            }
            System.out.println("MUTED?: " + avr.isMuted());
            System.out.println("VOL?: " + avr.volume());
            System.out.println("VOLUP: " + avr.volumeUp());
            System.out.println("VOLDOWN: " + avr.volumeDown());
            System.out.println("VOLSET505: " + avr.changeVolume(new Volume("55")));
            System.out.println("VOL?: " + avr.volume());
            System.out.println("INPUT: " + avr.inputSource());
            System.out.println("INPUTSET: "
                    + avr.selectInputSource(Sources.SAT_CBL));
            Thread.sleep(2000);
            System.out.println("INPUT?: " + avr.inputSource());
            System.out.println("PLAY IRADION: "
                    + avr.play(Playback.INTERNET_RADIO));
            System.out.println("SLEEPTIMER?: " + avr.isSleepTimerSet());
            System.out.println("SLEEPTIMERSET: " + avr.sleepTimer("010"));
            System.out.println("SLEEPTIMEROFF:" + avr.sleepTimerOff());

            // OSD support
            OSD osd = avr.createOSD();
            osd.show();
            osd.moveCursorDown();
            osd.moveCursorUp();
            osd.hide();
        } finally {
            avr.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        if (null == args || args.length != 2) {
            System.err.println("Try java -jar $/path/to/jar $host $port");
            System.exit(1);
        }
        System.out.println(String.format("Starting demo... Receiver: %s:%s",
                args[0], args[1]));
        Avr1912Demo test = new Avr1912Demo();
        test.demo(args[0], Integer.parseInt(args[1]));
        System.out.println("Demo done.");
    }
}
