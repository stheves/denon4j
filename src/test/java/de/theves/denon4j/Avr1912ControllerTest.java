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

import de.theves.denon4j.model.Command;
import de.theves.denon4j.net.NetClient;
import de.theves.denon4j.net.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class Avr1912ControllerTest {

    @Mock
    private NetClient netClient;

    private Avr1912Controller receiver;

    @Before
    public void setup() {
        receiver = new Avr1912Controller(netClient);
    }

    @Test
    public void basics() throws Exception {
        receiver.connect(1000);
        verify(netClient).connect(1000);

        receiver.isConnected();
        verify(netClient).isConnected();

        receiver.mute();
        verify(netClient).sendAndReceive(eq(new Command("MUTEON")));

        receiver.powerOff();
        verify(netClient).sendAndReceive(eq(new Command("PWSTANDBY")));

        receiver.disconnect();
        verify(netClient).disconnect();
    }

    @Test(expected = TimeoutException.class)
    public void testConnectShouldThrowATimeoutException() throws Exception {
        doThrow(TimeoutException.class).when(netClient).connect(1000);
        receiver.connect(1000);
    }

    @Test
    public void testSendGenericCommand() {
        Command command = new Command("FOO", Optional.of("BAR"));
        receiver.send(command);
        verify(netClient).sendAndReceive(same(command));
    }
}
