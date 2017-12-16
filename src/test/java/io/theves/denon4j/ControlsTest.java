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
        Setting<InputSource> si = denonAvr192.input();

        // execute control
        si.set(InputSource.SAT_CBL);

        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("SISAT/CBL".getBytes()));
            return null;
        }).when(protocol).send(cmd("SI?"));

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
        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("PWSTANDBY".getBytes()));
            return null;
        }).when(protocol).send(cmd("PW?"));
        assertThat(power.state()).isEqualTo("STANDBY");

        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("PWON".getBytes()));
            return null;
        }).when(protocol).send(cmd("PWON"));
        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("PWON".getBytes()));
            return null;
        }).when(protocol).send(cmd("PW?"));
        power.toggle();
        assertThat(power.state()).isEqualTo("ON");
    }

    @Test
    public void testMasterSlider() {
        Slider slider = denonAvr192.masterVolume();
        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("MV45".getBytes()));
            return null;
        }).when(protocol).send(cmd("MV?"));
        assertThat(slider.getValue()).isEqualTo("45");

        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("MV455".getBytes()));
            denonAvr192.dispatch(Event.create("MVMAX 68".getBytes()));
            return null;
        }).when(protocol).send(cmd("MVUP"));
        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("MV455".getBytes()));
            return null;
        }).when(protocol).send(cmd("MV?"));
        slider.slideUp();
        assertThat(slider.getValue()).isEqualTo("455");

        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("MV45".getBytes()));
            denonAvr192.dispatch(Event.create("MVMAX 675".getBytes()));
            return null;
        }).when(protocol).send(cmd("MVDOWN"));
        doAnswer(invocationOnMock -> {
            denonAvr192.dispatch(Event.create("MV45".getBytes()));
            return null;
        }).when(protocol).send(cmd("MV?"));
        slider.slideDown();
        assertThat(slider.getValue()).isEqualTo("45");
    }

    @Test
    public void testNetworkControl() {
        NetUsbIPodControl selectNetUsb = denonAvr192.netUsb();
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
                    .toArray(value -> new AbstractControl[denonAvr192.getControls().size()])
            );
    }
}
