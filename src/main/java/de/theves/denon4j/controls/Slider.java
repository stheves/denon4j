package de.theves.denon4j.controls;

/**
 * Slider control.
 *
 * @author stheves
 */
public interface Slider extends Control {
    void slideUp();

    void slideDown();

    String getValue();

    void set(String value);
}
