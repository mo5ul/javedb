package io.github.mohul.observability.info;
import java.nio.file.Path;
public final class EngineInfo {
    private final Path databasePath;
    private final long openedAt;
    private final EngineStatus status;
    public EngineInfo(Path databasePath, long openedAt, EngineStatus status) {
        this.databasePath = databasePath;
        this.openedAt = openedAt;
        this.status = status;
    }
    public Path getDatabasePath() {
        return databasePath;
    }
    public long getOpenedAt() {
        return openedAt;
    }
    public EngineStatus getStatus() {
        return status;
    }
}