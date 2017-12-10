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

package io.theves.denon4j;

import io.theves.denon4j.net.Event;

import java.util.List;

/**
 * Used to check if a response is completely received.
 *
 * @author stheves
 */
@FunctionalInterface
public interface CompletionCallback {
    boolean isComplete(List<Event> recorded);

    static CompletionCallback regex(String regex) {
        return recorded -> recorded.stream().anyMatch(e -> e.asciiValue().matches(regex));
    }

    static CompletionCallback completeWith(boolean result) {
        return recorded -> result;
    }
}
