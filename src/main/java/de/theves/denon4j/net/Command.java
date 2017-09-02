package de.theves.denon4j.net;

import java.time.LocalDateTime;

/**
 * Represents a command send to an AVR.
 *
 * @author stheves
 */
public interface Command extends Event {
    CommandId getId();

    LocalDateTime getExecutedAt();

    void execute();

    boolean isDirtying();

    String signature();
}
