package io.github.mohul.observability.storage;
import java.nio.file.Path;
public final class WALInfo {
    private final Path path;
    private final boolean exists;
    private final long sizeBytes;
    public WALInfo(Path path,boolean exists,long sizeBytes) {
        this.path=path;
        this.exists=exists;
        this.sizeBytes=sizeBytes;
    }
    public Path getPath() {
        return path;
    }
    public boolean exists() {
        return exists;
    }
    public long getSizeBytes() {
        return sizeBytes;
    }
}