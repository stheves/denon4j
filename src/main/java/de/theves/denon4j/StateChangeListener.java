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

import de.theves.denon4j.model.ReceiverState;
import de.theves.denon4j.net.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class description.
 *
 * @author Sascha Theves
 */
public class StateChangeListener implements EventConsumer {

    private final Logger logger = LoggerFactory.getLogger(StateChangeListener.class);

    private ReceiverState currentState;

    public StateChangeListener(ReceiverState currentState) {
        this.currentState = currentState;
    }

    @Override
    public void onEvent(String event) {
        synchronized (currentState) {
            if (event.startsWith("ZM")) {
                String value = event.substring(2);
                boolean oldValue = currentState.isMainZoneOn();
                boolean newValue = "ON".equals(value);
                if (oldValue != newValue) {
                    currentState.setMainZoneOn(newValue);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Recorded state change: Main Zone " + oldValue + " -> Main Zone " + newValue);
                    }
                }
            }
            if (event.startsWith("PW")) {
                String value = event.substring(2);
                boolean oldValue = currentState.isPowerOn();
                boolean newValue = "ON".equals(value);
                if (oldValue != newValue) {
                    currentState.setPowerOn(newValue);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Recorded state change: Power " + oldValue + " -> Power " + newValue);
                    }
                    currentState.setPowerOn("ON".equals(value));
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Current state: " + currentState);
            }
        }
    }

    public ReceiverState getCurrentState() {
        synchronized (currentState) {
            return currentState;
        }
    }
}
