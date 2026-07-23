package io.github.mohul.observability.statistics;
public final class StorageStatistics {
    private final long memTableSizeBytes;
    private final int memTableEntryCount;
    private final int sstableCount;
    private final long totalSSTableSizeBytes;
    public StorageStatistics(long memTableSizeBytes, int memTableEntryCount, int sstableCount, long totalSSTableSizeBytes) {
    this.memTableSizeBytes = memTableSizeBytes;this.memTableEntryCount = memTableEntryCount;this.sstableCount = sstableCount;this.totalSSTableSizeBytes = totalSSTableSizeBytes;
    }
    public long getMemTableSizeBytes() {
        return memTableSizeBytes;
    }
    public int getMemTableEntryCount() {
        return memTableEntryCount;
    }
    public int getSstableCount() {
        return sstableCount;
    }
    public long getTotalSSTableSizeBytes() {
        return totalSSTableSizeBytes;
    }
}