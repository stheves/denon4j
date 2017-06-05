package de.theves.denon4j.net;

import java.time.LocalDateTime;

/**
 * Represents a command send to an AVR.
 *
 * @author stheves
 */
public interface Command extends Event {
    LocalDateTime NEVER = LocalDateTime.MIN;

    CommandId getId();

    LocalDateTime getExecutedAt();

    void execute();
}
