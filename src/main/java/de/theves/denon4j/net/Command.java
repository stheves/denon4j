package de.theves.denon4j.net;

import java.time.LocalDateTime;

/**
 * Represents a command send to an AVR.
 *
 * @author Sascha Theves
 */
public interface Command extends Event {
    CommandId getId();

    LocalDateTime getExecutedAt();

    void execute();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();
}
