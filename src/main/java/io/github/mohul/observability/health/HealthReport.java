package io.github.mohul.observability.health;
public final class HealthReport {
    private final HealthStatus status;
    private final String message;
    public HealthReport(HealthStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    public HealthStatus getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}