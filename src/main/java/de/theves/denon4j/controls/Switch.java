package de.theves.denon4j.controls;

/**
 * Created by Elena on 04.06.2017.
 */
public interface Switch extends Control {
    void switchOff();

    void switchOn();

    boolean switchedOn();

    boolean switchedOff();

    String getOnValue();

    String getOffValue();
}
