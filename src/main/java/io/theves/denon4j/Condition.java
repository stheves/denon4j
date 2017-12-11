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

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * Used to check if a response is completely received.
 *
 * @author stheves
 */
@FunctionalInterface
public interface Condition {
    /**
     * Checks whether the response is completed.
     *
     * @param ctx the request context.
     * @return <code>true</code> if fulfilled.
     */
    boolean fullfilled(RequestContext ctx);

    /**
     * Condition that is fullfilled if the given regex matches any of the received events.
     *
     * @param regex the regex.
     * @return the regex condition.
     */
    static Condition regex(String regex) {
        return context -> context.received().stream().anyMatch(e -> e.asciiValue().matches(regex));
    }

    /**
     * Boolean condition that always returns the given <code>result</code>.
     *
     * @param result the result to return.
     * @return the boolean condition.
     */
    static Condition bool(boolean result) {
        return context -> result;
    }

    static Condition retries(int retries) {
        return context -> context.counter() < retries;
    }

    static Condition duration(Duration duration) {
        return context -> Duration.between(context.start(), Instant.now()).compareTo(duration) > 0;
    }

    static Condition anyMatch(Condition... children) {
        return ctx -> Stream.of(children).anyMatch(c -> c.fullfilled(ctx));
    }
}
