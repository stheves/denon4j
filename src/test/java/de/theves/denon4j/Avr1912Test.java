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

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.model.Command;
import de.theves.denon4j.net.NetClient;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Avr1912Test {
    @Test
    public void basics() throws Exception {
        NetClient netClient = mock(NetClient.class);
        Avr1912 receiver = new Avr1912(netClient);

        receiver.connect(1000);
        verify(netClient).connect(1000);

        receiver.mute();
        verify(netClient).sendAndReceive(eq(new Command("MUTEON")), any(Optional.class));

        receiver.powerOff();
        verify(netClient).sendAndReceive(eq(new Command("PWSTANDBY")), any(Optional.class));

        receiver.disconnect();
        verify(netClient).disconnect();
    }
}
