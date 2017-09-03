package de.theves.denon4j.controls;

/**
 * Control to switch between sources.
 *
 * @author stheves
 */
public interface Select<S extends Enum> extends Control {
    /**
     * Selects the given <code>select</code>.
     *
     * @param source the select to select.
     */
    void select(S source);

    /**
     * Returns the current active select.
     *
     * @return the current select.
     */
    S get();
}
