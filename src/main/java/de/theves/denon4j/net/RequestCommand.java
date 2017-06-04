package de.theves.denon4j.net;

/**
 * Command that has a response.
 *
 * @author Sascha Theves
 */
public interface RequestCommand extends Event, Command {
    Event getReceived();
}
