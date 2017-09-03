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

package de.theves.denon4j.controls;

import de.theves.denon4j.internal.net.Command;
import de.theves.denon4j.internal.net.Event;
import de.theves.denon4j.net.Protocol;

/**
 * Class description.
 *
 * @author stheves
 */
public class Menu extends AbstractControl {
    public Menu(Protocol protocol) {
        super("MN", protocol);
        setName("Main Menu");
    }

    @Override
    protected void doHandle(Event event) {
        // not needed
    }

    @Override
    protected void doInit() {
    }

    public void control(MenuControls controls) {
        Command command = CommandFactory.createCommand(protocol, prefix, controls.toString());
        command.execute();
    }
}
