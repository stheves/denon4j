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
    boolean fulfilled(RecvContext ctx);

    /**
     * Condition that is fullfilled if the given regex matches any of the received events.
     *
     * @param regex the regex.
     * @return the regex condition.
     */
    static Condition regex(String regex) {
        return context ->
            context.received().stream()
                .anyMatch(e -> e.asciiValue().matches(regex));
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

    /**
     * Returns <code>true</code> if the given amount of retries has been reached.
     *
     * @param retries the amount of retries to check.
     * @return the condition.
     */
    static Condition retries(int retries) {
        return context -> context.counter() > retries;
    }

    /**
     * Is fulfilled when the duration elapsed.
     *
     * @param duration the duration.
     * @return the condition.
     */
    static Condition duration(Duration duration) {
        return context ->
            Duration.between(context.start(), Instant.now())
                .compareTo(duration) > 0;
    }

    /**
     * Is fulfilled if the response has the given <code>size</code>.
     *
     * @param size the size to check.
     * @return the condition.
     */
    static Condition size(int size) {
        return ctx -> ctx.received().size() >= size;
    }

    /**
     * Matches any of the given conditions.
     *
     * @param conditions the conditions to match.
     * @return the condition.
     */
    static Condition anyMatch(Condition... conditions) {
        return ctx -> Stream.of(conditions).anyMatch(c -> c.fulfilled(ctx));
    }

    /**
     * Matches all of the given <code>conditions</code>.
     *
     * @param conditions the conditions to match.
     * @return the condition.
     */
    static Condition allMatch(Condition... conditions) {
        return ctx -> Stream.of(conditions).allMatch(c -> c.fulfilled(ctx));
    }
}
