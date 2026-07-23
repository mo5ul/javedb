package io.github.mohul.observability.info;
import java.nio.file.Path;
public final class DatabaseConfiguration {
    private final Path databasePath;
    private final long memTableMaxSizeBytes;
    private final boolean autoFlushEnabled;
    public DatabaseConfiguration(Path databasePath,long memTableMaxSizeBytes,boolean autoFlushEnabled) {
        this.databasePath=databasePath;
        this.memTableMaxSizeBytes=memTableMaxSizeBytes;
        this.autoFlushEnabled=autoFlushEnabled;
    }
    public Path getDatabasePath() {
        return databasePath;
    }
    public long getMemTableMaxSizeBytes() {
        return memTableMaxSizeBytes;
    }
    public boolean isAutoFlushEnabled() {
        return autoFlushEnabled;
    }
}
