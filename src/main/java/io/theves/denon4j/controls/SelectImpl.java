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

package io.theves.denon4j.controls;

import io.theves.denon4j.DenonReceiver;
import io.theves.denon4j.net.Event;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * SelectImpl control.
 *
 * @author stheves
 */
public class SelectImpl<S extends Enum> extends AbstractControl implements Select<S> {
    private final S[] params;

    public SelectImpl(DenonReceiver receiver, String prefix, S[] params) {
        super(receiver, prefix);
        this.params = Objects.requireNonNull(params);
    }

    @Override
    public void select(S selection) {
        send(selection.toString());
    }

    @Override
    public S get() {
        Event received = sendRequest();
        return findSource(received);
    }

    private S findSource(Event state) {
        String value = state.asciiValue().substring(2);
        return Stream.of(params)
            .filter(param -> value.equals(param.toString()))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    @Override
    protected void doHandle(Event event) {

    }

}
