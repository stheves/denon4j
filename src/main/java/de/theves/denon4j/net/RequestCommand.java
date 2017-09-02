package de.theves.denon4j.net;

/**
 * Command that has a response.
 *
 * @author stheves
 */
public interface RequestCommand extends Command {
    Event getReceived();
}
