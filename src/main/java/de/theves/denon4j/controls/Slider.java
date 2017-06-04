package de.theves.denon4j.controls;

/**
 * Created by Elena on 04.06.2017.
 */
public interface Slider extends Control {
    void slideUp();

    void slideDown();

    String getValue();

    void set(String value);
}
