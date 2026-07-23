package io.github.mohul.observability.event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public final class EngineEventLog {
    private static final int MAX_EVENTS = 1000;
    private final List<EngineEvent> events = new ArrayList<>();
    public void addEvent(String type, String message) {
        if (events.size() == MAX_EVENTS) {
            events.remove(0);
        }
        events.add(new EngineEvent(type, message));
    }
    public List<EngineEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
    public void clear() {
        events.clear();
    }
}