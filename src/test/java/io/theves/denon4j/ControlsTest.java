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

import io.theves.denon4j.controls.*;
import io.theves.denon4j.net.Command;
import io.theves.denon4j.net.ConnectException;
import io.theves.denon4j.net.Event;
import io.theves.denon4j.net.Protocol;
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
    private DenonReceiver denonAvr192;
    private Protocol protocol;

    @Before
    public void setup() {
        protocol = mock(Protocol.class);
        denonAvr192 = new DenonReceiver(protocol);
    }

    @Test
    public void testConnectionHandling() {
        denonAvr192.connect(100);
        InOrder order = inOrder(protocol, protocol);
        order.verify(protocol, times(1)).establishConnection(100);

        when(protocol.isConnected()).thenReturn(Boolean.TRUE);
        assertThat(denonAvr192.isConnected()).isTrue();

        denonAvr192.disconnect();
        verify(protocol).disconnect();

        when(protocol.isConnected()).thenReturn(Boolean.FALSE);
        assertThat(denonAvr192.isConnected()).isFalse();

        doThrow(new ConnectException("Failure")).when(protocol).establishConnection(137);
        assertThatThrownBy(() -> denonAvr192.connect(137)).isInstanceOf(ConnectException.class).withFailMessage("Failure");
    }

    @Test
    public void testSelectControl() {
        Select<InputSource> si = denonAvr192.input();

        // execute control
        si.select(InputSource.SAT_CBL);

        denonAvr192.getEventDispatcher().dispatch(Event.create("SISAT/CBL".getBytes()));
        String source = si.get();
        assertThat(source).isEqualTo(InputSource.SAT_CBL.getInputSource());

        Command cmd = cmd("SISAT/CBL");
        verify(protocol).send(cmd);
    }

    private Command cmd(String s) {
        return Command.createCommand(s.substring(0, 2), s.substring(2));
    }

    @Test
    public void testPowerControl() {
        Toggle power = denonAvr192.power();
        denonAvr192.getEventDispatcher().dispatch(Event.create("PWSTANDBY".getBytes()));
        assertThat(power.state()).isEqualTo(SwitchState.STANDBY);
        power.toggle();
        denonAvr192.getEventDispatcher().dispatch(Event.create("PWON".getBytes()));
        verify(protocol).send(cmd("PWON"));
    }

    @Test
    public void testMasterSlider() {
        Slider slider = denonAvr192.masterVolume();
        denonAvr192.getEventDispatcher().dispatch(Event.create("MV45".getBytes()));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.slideUp();
        // fake events
        denonAvr192.getEventDispatcher().dispatch(Event.create("MV455".getBytes()));
        denonAvr192.getEventDispatcher().dispatch(Event.create("MVMAX 68".getBytes()));
        verify(protocol).send(cmd("MVUP"));
        assertThat(slider.getValue()).isEqualTo("455");

        slider.slideDown();
        denonAvr192.getEventDispatcher().dispatch(Event.create("MV45".getBytes()));
        denonAvr192.getEventDispatcher().dispatch(Event.create("MVMAX 675".getBytes()));
        verify(protocol).send(cmd("MVDOWN"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.set("55");
        denonAvr192.getEventDispatcher().dispatch(Event.create("MV55".getBytes()));
        denonAvr192.getEventDispatcher().dispatch(Event.create("MVMAX 72".getBytes()));
        verify(protocol).send(Command.createCommand("MV55"));
        assertThat(slider.getValue()).isEqualTo("55");
    }

    @Test
    public void testNetworkControl() {
        NetUsbIPod selectNetUsb = denonAvr192.netUsb();
        assertThat(selectNetUsb.getCommandPrefix()).isEqualTo("NS");

        selectNetUsb.cursorDown();
        verify(protocol).send(cmd("NS91"));
    }

    @Test
    public void testCorrectInit() {
        assertDispatcherValid();
    }

    private void assertDispatcherValid() {
        assertThat(denonAvr192.getEventListeners())
            .containsExactlyInAnyOrder(
                denonAvr192.getControls()
                    .stream()
                    .toArray(value -> new Control[denonAvr192.getControls().size()])
            );
    }
}
