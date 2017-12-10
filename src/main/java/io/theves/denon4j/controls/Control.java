/*
 * Copyright 2017 Sascha Theves
 *
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

package io.theves.denon4j.controls;

import io.theves.denon4j.net.EventListener;

/**
 * A control represents a feature of an {@link io.theves.denon4j.DenonReceiver}.
 *
 * @author stheves
 */
public interface Control extends EventListener {

    /**
     * Returns the command prefix which this control handles.
     *
     * @return the command prefix of this control
     */
    String getCommandPrefix();

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

}
