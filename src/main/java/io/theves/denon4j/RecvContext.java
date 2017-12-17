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
    private static final Duration NOT_STARTED = Duration.ofMillis(0L);

    private Instant start;
    private Instant end;
    private List<Event> received;
    private boolean receiving;
    private Condition condition;

    RecvContext(Condition condition) {
        this.start = Instant.now();
        this.received = new ArrayList<>();
        this.condition = condition;
    }

    public List<Event> received() {
        return received;
    }


    void endReceive() {
        receiving = false;
        end = Instant.now();
    }

    void beginReceive() {
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
