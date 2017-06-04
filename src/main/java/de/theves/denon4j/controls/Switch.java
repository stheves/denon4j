package de.theves.denon4j.controls;

/**
 * Switch control.
 *
 * @author stheves
 */
public interface Switch extends Control {
    void switchOff();

    void switchOn();

    SwitchState getSwitchState();
}
