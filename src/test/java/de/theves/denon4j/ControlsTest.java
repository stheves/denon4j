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
import de.theves.denon4j.internal.controls.NetControlImpl;
import de.theves.denon4j.internal.net.EventImpl;
import de.theves.denon4j.net.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for basic controls.
 */
public class ControlsTest {
    private AVR1912 avr1912;
    private Protocol protocol;
    private CommandRegistry registry;

    @Before
    public void setup() {
        protocol = mock(Protocol.class);
        avr1912 = new AVR1912(protocol);
        registry = avr1912.getRegistry();
    }

    @Test
    public void testConnectionHandling() {
        avr1912.connect(100);
        InOrder order = inOrder(protocol, protocol);
        order.verify(protocol, times(1)).setListener(avr1912.getEventDispatcher());
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
        List<Command> commands = si.getCommands();
        assertThat(commands.size()).isEqualTo(InputSource.values().length + 1);
        assertThat(registry.findByPrefix("SI")).hasSize(InputSource.values().length + 1);

        // execute control
        si.source(InputSource.SAT_CBL);

        when(protocol.request((RequestCommand) cmd("SI?"))).thenReturn(event("SISAT/CBL"));
        InputSource source = si.getSource();
        assertThat(source).isEqualTo(InputSource.SAT_CBL);

        Command cmd = cmd("SISAT/CBL");
        verify(protocol).send(cmd);
        assertThat(cmd.getExecutedAt()).isAfter(LocalDateTime.MIN);
    }

    @Test
    public void testPowerControl() {
        Toggle power = avr1912.power();
        assertThat(power.getCommands())
                .hasSize(3)
                .containsExactlyInAnyOrder(commands("PWON", "PWSTANDBY", "PW?"));

        when(protocol.request((RequestCommand) cmd("PW?")))
                .thenReturn(event("PWSTANDBY"), event("PWON"));

        assertThat(power.state()).isEqualTo(SwitchState.STANDBY);
        power.toggle();
        verify(protocol).send(cmd("PWON"));
        assertThat(power.state()).isEqualTo(SwitchState.ON);
        power.toggle();
        verify(protocol).send(cmd("PWSTANDBY"));
    }

    @Test
    public void testMuteControl() {
        Toggle mute = avr1912.mute();
        assertThat(mute.getCommands()).containsExactlyInAnyOrder(commands("MUON", "MUOFF", "MU?"));
    }

    @Test
    public void testUnknownCommand() {
        assertThatThrownBy(() ->
                registry.getCommand(CommandId.random())).isInstanceOf(CommandNotFoundException.class);
    }

    @Test
    public void testMasterSlider() {
        Slider slider = avr1912.masterVolume();
        assertThat(slider.getCommands()).hasSize(4).containsExactlyInAnyOrder(commands("MVUP", "MVDOWN", "MV", "MV?"));
        when(protocol.request((RequestCommand) cmd("MV?"))).thenReturn(event("MV45"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.slideUp();
        // fake events
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MV455"));
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MVMAX 68"));
        verify(protocol).send(cmd("MVUP"));
        assertThat(slider.getValue()).isEqualTo("455");

        slider.slideDown();
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MV45"));
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MVMAX 675"));
        verify(protocol).send(cmd("MVDOWN"));
        assertThat(slider.getValue()).isEqualTo("45");

        slider.set("55");
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MV55"));
        avr1912.getEventDispatcher().onEvent(EventImpl.create("MVMAX 72"));
        verify(protocol).send(cmd("MV55"));
        assertThat(slider.getValue()).isEqualTo("55");

        // test validity checks
        assertThat(slider.isValid()).isTrue();
        assertThatThrownBy(() -> slider.set("invalid")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set("MV55")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set(" 55")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set("5")).isInstanceOf(InvalidSignatureException.class);
        assertThatThrownBy(() -> slider.set(" 5 ")).isInstanceOf(InvalidSignatureException.class);
    }

    @Test
    public void testMainZoneToggle() {
        Toggle toggle = avr1912.mainZone();
        assertThat(toggle.getCommands()).hasSize(3).containsExactlyInAnyOrder(commands("ZMON", "ZMOFF", "ZM?"));
    }

    @Test
    public void testNetworkControl() {
        NetControlImpl selectNetworkControl = avr1912.selectNetworkControl();
        assertThat(selectNetworkControl.getCommands()).hasSize(NetControls.values().length);
        assertThat(selectNetworkControl.getCommandPrefix()).isEqualTo("NS");
        assertThat(registry.findBySignature(() -> "NS?")).isEmpty();

        selectNetworkControl.control(NetControls.CURSOR_DOWN);
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

    private Event event(String e) {
        return EventImpl.create(e);
    }

    private Command[] commands(String... sig) {
        List<Command> commandList = Stream.of(sig).map(this::cmd).collect(Collectors.toList());
        return commandList.toArray(new Command[commandList.size()]);
    }

    private Command cmd(String s) {
        return registry.findBySignature(
                () -> s
        ).orElseThrow(
                () -> new CommandNotFoundException("Command with signature '" + s + "' not found")
        );
    }
}
