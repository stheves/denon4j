package de.theves.denon4j.controls;

import java.util.Optional;

/**
 * Created by Elena on 04.06.2017.
 */
public interface Select<S extends Enum<S>> extends Control {
    void select(S source);

    Optional<S> getSource();
}
