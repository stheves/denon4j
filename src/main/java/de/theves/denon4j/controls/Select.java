package de.theves.denon4j.controls;

/**
 * Control to switch between sources.
 *
 * @author stheves
 */
public interface Select<S extends Enum> extends Control {
    /**
     * Selects the given <code>source</code>.
     *
     * @param source the source to select.
     */
    void select(S source);

    /**
     * Returns the current active source.
     *
     * @return the current source.
     */
    S getSource();
}
