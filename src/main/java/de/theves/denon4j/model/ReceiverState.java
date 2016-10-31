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

package de.theves.denon4j.model;

/**
 * Represents the state of a receiver.
 *
 * @author Sascha Theves
 */
public class ReceiverState {
    private Boolean powerOn;
    private Boolean mainZoneOn;

    public boolean isMainZoneOn() {
        return mainZoneOn != null && mainZoneOn;
    }

    public boolean isPowerOn() {
        return powerOn != null && powerOn;
    }

    public void setMainZoneOn(boolean mainZoneOn) {
        this.mainZoneOn = mainZoneOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiverState that = (ReceiverState) o;

        if (powerOn != that.powerOn) return false;
        return mainZoneOn == that.mainZoneOn;

    }

    @Override
    public int hashCode() {
        int result = (powerOn ? 1 : 0);
        result = 31 * result + (mainZoneOn ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReceiverState{" +
                "powerOn=" + powerOn +
                ", mainZoneOn=" + mainZoneOn +
                '}';
    }
}
