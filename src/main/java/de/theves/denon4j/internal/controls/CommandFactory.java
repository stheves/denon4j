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

package de.theves.denon4j.internal.controls;

import de.theves.denon4j.internal.net.*;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.CommandId;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.Protocol;

import java.util.regex.Pattern;

import static de.theves.denon4j.internal.net.ParameterImpl.createParameter;

/**
 * Factory for commands.
 *
 * @author stheves
 */
class CommandFactory {

    private CommandFactory() {
    }

    static Command createCommand(Protocol protocol, String prefix, String param) {
        if (null != prefix) {
            CommandId commandId = CommandId.random();
            if (param.contains("[")) {
                return new SetCommandImpl(protocol, commandId, prefix, parsePattern(param));
            } else if (isRequest(prefix, param)) {
                return new RequestCommandImpl(protocol, commandId, prefix);
            } else if (isOnscreenInfoRequest(prefix, param)) {
                return new OnscreenInfoRequest(protocol, commandId, prefix, createParameter(param));
            }
            return new CommandImpl(protocol, commandId, prefix, createParameter(param));
        }
        throw new IllegalArgumentException("Command may not be null");
    }

    private static Pattern parsePattern(String param) {
        int begin = param.indexOf("[");
        int end = param.lastIndexOf("]");
        return Pattern.compile(param.substring(begin + 1, end));
    }

    private static boolean isRequest(String prefix, String param) {
        return param.equals(ParameterImpl.REQUEST.getValue());
    }

    private static boolean isOnscreenInfoRequest(String prefix, String param) {
        return "NSE".equals(prefix + param);
    }
}
