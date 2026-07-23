package io.github.mohul.observability.event;
public final class EngineEvent {
    private final long timestamp;
    private final String type;
    private final String message;
    public EngineEvent(String type, String message) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.message = message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }
}