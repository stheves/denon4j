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

import de.theves.denon4j.model.Command;

/**
 * Represents the on screen display. Can be used to browse the system menu and get information about what`s
 * currently on air.
 *
 * @author Sascha Theves
 */
public class MainMenu {
    private GenericController receiver;

    public MainMenu(GenericController receiver) {
        this.receiver = receiver;
    }

    public void moveCursorDown() {
        this.receiver.send(new Command("MNCDN"));
    }

    public void moveCursorUp() {
        this.receiver.send(new Command("MNCUP"));
    }

    public void moveCursorLeft() {
        this.receiver.send(new Command("MNCLT"));
    }

    public void moveCursorRight() {
        this.receiver.send(new Command("MNCRT"));
    }

    public void enter() {
        this.receiver.send(new Command("MNENT"));
    }

    public void showMenu() {
        this.receiver.send(new Command("MNMEN ON"));
    }

    public void hideMenu() {
        this.receiver.send(new Command("MNMEN OFF"));
    }
}
