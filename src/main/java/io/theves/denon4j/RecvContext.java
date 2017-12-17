package io.theves.denon4j;

import io.theves.denon4j.net.Event;

import java.time.Duration;
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
    public static final Duration NOT_STARTED = Duration.ofMillis(0L);
    private int counter;
    private Instant start;
    private Instant end;
    private List<Event> received;
    private boolean receiving;
    private Condition condition;

    public RecvContext(Condition condition) {
        this.counter = 0;
        this.start = Instant.now();
        this.received = new ArrayList<>();
        this.condition = condition;
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
        receiving = false;
        end = Instant.now();
    }

    public void beginReceive() {
        receiving = true;
        start = Instant.now();
    }

    public boolean isReceiving() {
        return receiving;
    }

    public boolean fulfilled() {
        return condition.fulfilled(this);
    }

    public Duration duration() {
        if (end != null && start != null) {
            return Duration.between(start, end);
        }
        return NOT_STARTED;
    }

}
