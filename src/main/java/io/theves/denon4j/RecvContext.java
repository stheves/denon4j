package io.theves.denon4j;

import io.theves.denon4j.net.Event;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Context when receiving a response.
 * Clients get this when {@link Condition#fulfilled(RecvContext)} is called.
 *
 * @author stheves
 */
public class RecvContext {
    private int counter;
    private Instant start;
    private List<Event> received;
    private boolean receiving;

    public RecvContext() {
        this.counter = 0;
        this.start = Instant.now();
        this.received = new ArrayList<>();
    }

    public int counter() {
        return counter;
    }

    public Instant start() {
        return start;
    }

    public List<Event> received() {
        return received;
    }

    public void incrementCounter() {
        counter++;
    }

    public void endReceive() {
        counter = 0;
        received.clear();
        receiving = false;
    }

    public void beginReceive() {
        receiving = true;
        start = Instant.now();
    }

    public boolean isReceiving() {
        return receiving;
    }
}
