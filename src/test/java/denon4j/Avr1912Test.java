/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package denon4j;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.theves.denon4j.Avr1912;

public class Avr1912Test {
    @Test
    public void basics() throws Exception {
        AvrMock mock = new AvrMock();
        mock.start();

        // mock chooses host and a free port by itself
        Avr1912 receiver = new Avr1912("0.0.0.0", mock.getPort());
        receiver.connect(1000);
        receiver.mute();
        String lastCommand = mock.getCommandString();
        assertEquals("MUON", lastCommand);

        receiver.volumeUp();
        lastCommand = mock.getCommandString();
        assertEquals("MVUP", lastCommand);

        receiver.powerOff();
        lastCommand = mock.getCommandString();
        assertEquals("PWSTANDBY", lastCommand);

        mock.stop();
    }
}
