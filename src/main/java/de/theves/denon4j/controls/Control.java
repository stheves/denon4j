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

import de.theves.denon4j.Receiver;
import de.theves.denon4j.net.Event;

/**
 * A control represents a feature of an {@link Receiver}.
 *
 * @author stheves
 */
public interface Control {
    /**
     * Invoked when an event belonging to this control`s prefix was received on the event bus.
     *
     * @param event the event that was received.
     */
    void handle(Event event);

    /**
     * Returns the command prefix which this control handles.
     *
     * @return the command prefix of this control
     */
    String getCommandPrefix();

    /**
     * Initialized this control.
     */
    void init();

    /**
     * Returns <code>true</code> if this control is initialized.
     *
     * @return <code>true</code> if initialized.
     */
    boolean isInitialized();

    /**
     * Disposes this control and frees resources.
     */
    void dispose();

    /**
     * Returns the name of the control.
     *
     * @return the name.
     */
    String getName();

    /**
     * Sets the name of this control.
     *
     * @param name the name.
     */
    void setName(String name);

    /**
     * Returns <code>true</code> if this control can handle the <code>event</code>.
     *
     * @param event the event to check.
     * @return <code>true</code> if this control can handle the event.
     */
    boolean supports(Event event);
}
