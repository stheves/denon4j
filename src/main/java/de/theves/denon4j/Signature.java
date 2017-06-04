package de.theves.denon4j;

/**
 * Represents a command`s signature e.g. 'SISAT/CBL' for Select INPUT SAT/CABLE.
 *
 * @author Sascha Theves
 */
@FunctionalInterface
public interface Signature {
    String signature();
}
