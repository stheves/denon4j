package de.theves.denon4j.controls;

/**
 * Represents a command`s signature e.g. 'SISAT/CBL' for SelectImpl INPUT SAT/CABLE.
 *
 * @author stheves
 */
@FunctionalInterface
public interface Signature {
    String signature();
}
