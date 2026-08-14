package io.github.mohul.observability.storage;

public final class CompactionResult {
    private final int mergedSSTables;
    private final int liveRecords;
    private final int tombstonesRemoved;
    private final String newSSTable;
    public CompactionResult(
            int mergedSSTables,
            int liveRecords,
            int tombstonesRemoved,
            String newSSTable) {
        this.mergedSSTables = mergedSSTables;
        this.liveRecords = liveRecords;
        this.tombstonesRemoved = tombstonesRemoved;
        this.newSSTable = newSSTable;
    }
    public int getMergedSSTables() {
        return mergedSSTables;
    }
    public int getLiveRecords() {
        return liveRecords;
    }
    public int getTombstonesRemoved() {
        return tombstonesRemoved;
    }
    public String getNewSSTable() {
        return newSSTable;
    }
}