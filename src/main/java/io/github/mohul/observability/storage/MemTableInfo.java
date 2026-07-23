package io.github.mohul.observability.storage;
public final class MemTableInfo {
    private final int entryCount;
    private final long estimatedSizeBytes;
    public MemTableInfo(int entryCount,long estimatedSizeBytes) {
        this.entryCount=entryCount;
        this.estimatedSizeBytes=estimatedSizeBytes;
    }
    public int getEntryCount() {
        return entryCount;
    }
    public long getEstimatedSizeBytes() {
        return estimatedSizeBytes;
    }
}