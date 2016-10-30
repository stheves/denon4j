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

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class OSD {
    private AbstractAvReceiver receiver;

    public OSD(AbstractAvReceiver receiver) {
        this.receiver = receiver;
    }

    public DisplayInfo getDisplayInfo() {
        return new DisplayInfo(this.receiver.send("NSE"));
    }

    public void moveCursorDown() {
        this.receiver.send("MNCDN");
    }

    public void moveCursorUp() {
        this.receiver.send("MNCUP");
    }

    public void moveCursorLeft() {
        this.receiver.send("MNCLT");
    }

    public void moveCursorRight() {
        this.receiver.send("MNCRT");
    }

    public void enter() {
        this.receiver.send("MNENT");
    }

    public void show() {
        this.receiver.send("MNMEN ON");
    }

    public void hide() {
        this.receiver.send("MNMEN OFF");
    }
}
