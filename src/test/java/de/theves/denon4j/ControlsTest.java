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

import de.theves.denon4j.controls.*;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.ConnectException;
import de.theves.denon4j.net.Event;
import de.theves.denon4j.net.Protocol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for basic controls.
 */
public class ControlsTest {
    private AVR1912 avr1912;
    private Protocol protocol;

    @Before
    public void setup() {
        protocol = mock(Protocol.class);
        avr1912 = new AVR1912(protocol);
    }

    @Test
    public void testConnectionHandling() {
        avr1912.connect(100);
        InOrder order = inOrder(protocol, protocol);
        order.verify(protocol, times(1)).setDispatcher(avr1912.getEventDispatcher());
        order.verify(protocol, times(1)).establishConnection(100);

        when(protocol.isConnected()).thenReturn(Boolean.TRUE);
        assertThat(avr1912.isConnected()).isTrue();

        avr1912.disconnect();
        verify(protocol).disconnect();

        when(protocol.isConnected()).thenReturn(Boolean.FALSE);
        assertThat(avr1912.isConnected()).isFalse();

        doThrow(new ConnectException("Failure")).when(protocol).establishConnection(137);
        assertThatThrownBy(() -> avr1912.connect(137)).isInstanceOf(ConnectException.class).withFailMessage("Failure");
    }

    @Test
    public void testSelectControl() {
        Select<InputSource> si = avr1912.input();

        // execute control
        si.source(InputSource.SAT_CBL);

        when(protocol.request(cmd("SI?"))).thenReturn(event("SISAT/CBL"));
        InputSource source = si.getSource();
        assertThat(source).isEqualTo(InputSource.SAT_CBL);

        Command cmd = cmd("SISAT/CBL");
        verify(protocol).send(cmd);
    }

    private Command cmd(String s) {
        return Command.createCommand(protocol, s.substring(0, 2), s.substring(2));
    }

    private Event event(String e) {
        return Event.create(e);
    }

    @Test
    public void testPowerControl() {
        Toggle power = avr1912.power();

        when(protocol.request(cmd("PW?")))
                .thenReturn(event("PWSTANDBY"));

        assertThat(power.state()).isEqualTo(SwitchState.STANDBY);
        power.toggle();
        verify(protocol).send(cmd("PWON"));
    }

    @Test
    public void testMasterSlider() {
        Slider slider = avr1912.masterVolume();
        when(protocol.request(cmd("MV?"))).thenReturn(event("MV45"), event("MV455"), event("MV45"), event("MV55"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.slideUp();
        // fake events
        avr1912.getEventDispatcher().dispatch(Event.create("MV455"));
        avr1912.getEventDispatcher().dispatch(Event.create("MVMAX 68"));
        verify(protocol).send(cmd("MVUP"));
        assertThat(slider.getValue()).isEqualTo("455");

        slider.slideDown();
        avr1912.getEventDispatcher().dispatch(Event.create("MV45"));
        avr1912.getEventDispatcher().dispatch(Event.create("MVMAX 675"));
        verify(protocol).send(cmd("MVDOWN"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.set("55");
        avr1912.getEventDispatcher().dispatch(Event.create("MV55"));
        avr1912.getEventDispatcher().dispatch(Event.create("MVMAX 72"));
        verify(protocol).send(Command.createSetCommand(protocol, "MV"));
        assertThat(slider.getValue()).isEqualTo("55");
    }

    @Test
    public void testNetworkControl() {
        NetworkControl selectNetworkControl = avr1912.networkControl();
        assertThat(selectNetworkControl.getCommandPrefix()).isEqualTo("NS");

        selectNetworkControl.control(NetworkControls.CURSOR_DOWN);
        verify(protocol).send(cmd("NS91"));
    }

    @Test
    public void testCorrectInit() {
        // check all controls initialized
        assertControlsInitialized();

        // and added to dispatcher
        assertDispatcherValid();
    }

    private void assertControlsInitialized() {
        assertThat(avr1912.getControls()
                .stream()
                .filter(c -> !c.isInitialized())
                .count()).isEqualTo(0);
    }

    private void assertDispatcherValid() {
        assertThat(avr1912.getEventDispatcher().getControls())
                .containsExactlyInAnyOrder(
                        avr1912.getControls()
                                .stream()
                                .toArray(value -> new Control[avr1912.getControls().size()])
                );
    }
}
