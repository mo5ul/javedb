package io.github.mohul.memtable;
import io.github.mohul.util.ByteArrayComparator;
import java.util.NavigableMap;
import java.util.TreeMap;
public final class MemTable {
    private final NavigableMap<byte[], Entry> entries;
    private static final int ENTRY_OVERHEAD = 32;
    private long estimatedSizeInBytes;
    public MemTable() {
        this.entries = new TreeMap<>(new ByteArrayComparator());
    }
    public void put(Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry cannot be null.");
        }
        Entry previous = entries.put(entry.getKey(), entry);
        if(previous==null){
            estimatedSizeInBytes += estimateEntrySize(entry);
        } else {
            estimatedSizeInBytes -= estimateEntrySize(previous);
            estimatedSizeInBytes += estimateEntrySize(entry);
        }
    }
    public Entry get(byte[] key) {
        validateKey(key);
        return entries.get(key);
    }
    public void delete(byte[] key) {
        validateKey(key);
        Entry removed = entries.remove(key);
        if(removed != null){
            estimatedSizeInBytes -= estimateEntrySize(removed);
        }
    }
    public int size() {
        return entries.size();
    }
    private void validateKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("Key cannot be empty.");
        }
    }
    public Iterable<Entry> entries(){
        return entries.values();
    }
    public long getEstimatedSizeInBytes() {
        return estimatedSizeInBytes;
    }
    private long estimateEntrySize(Entry entry) {
        return ENTRY_OVERHEAD
                + entry.getKey().length
                + entry.getValue().length;
    }
}