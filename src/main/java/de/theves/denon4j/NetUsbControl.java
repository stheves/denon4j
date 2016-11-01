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
import de.theves.denon4j.model.DisplayInfo;
import de.theves.denon4j.model.Response;
import de.theves.denon4j.net.ResponseParser;

import java.util.Objects;
import java.util.Optional;

/**
 * Network audio/USB/iPod DIRECT extended control.
 *
 * @author Sascha Theves
 */
public class NetUsbControl {
    private GenericController controller;
    private ResponseParser parser;

    public NetUsbControl(GenericController controller, ResponseParser parser) {
        this.controller = Objects.requireNonNull(controller);
        this.parser = Objects.requireNonNull(parser);
    }

    public void cursorUp() {
        controller.send(new Command("NS90"));
    }

    public void cursorDown() {
        controller.send(new Command("NS91"));
    }

    public void cursorLeft() {
        controller.send(new Command("NS92"));
    }

    public void cursorRight() {
        controller.send(new Command("NS93"));
    }

    public void enter() {
        controller.send(new Command("NS94"));
    }

    public void play() {
        controller.send(new Command("NS9A"));
    }

    public void pause() {
        controller.send(new Command("NS9B"));
    }

    public void stop() {
        controller.send(new Command("NS9C"));
    }

    public void skipPlus() {
        controller.send(new Command("NS9D"));
    }

    public void skipMinus() {
        controller.send(new Command("NS9E"));
    }

    public void repeatOne() {
        controller.send(new Command("NS9H"));
    }

    public void repeatAll() {
        controller.send(new Command("NS9I"));
    }

    public void repeatOff() {
        controller.send(new Command("NS9J"));
    }

    public void shuffle() {
        controller.send(new Command("NS9K"));
    }

    public void shuffleOff() {
        controller.send(new Command("NS9M"));
    }

    public void toggleControllerMode() {
        controller.send(new Command("NS9W"));
    }

    public void nextPage() {
        controller.send(new Command("NS9X"));
    }

    public void previousPage() {
        controller.send(new Command("NS9Y"));
    }

    public void togglePartyMode() {
        controller.send(new Command("NSPT"));
    }

    public DisplayInfo getInfo() {
        Optional<Response> response = controller.send(new Command("NSE"));
        return parser.parseDisplayInfo(response);
    }
}
