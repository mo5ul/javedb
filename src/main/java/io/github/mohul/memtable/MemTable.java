package io.github.mohul.memtable;
import io.github.mohul.util.ByteArrayComparator;
import java.util.NavigableMap;
import java.util.TreeMap;
public final class MemTable {
    private final NavigableMap<byte[], Entry> entries;
    public MemTable() {
        this.entries = new TreeMap<>(new ByteArrayComparator());
    }
    public void put(Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry cannot be null.");
        }
        entries.put(entry.getKey(), entry);
    }
    public Entry get(byte[] key) {
        validateKey(key);
        return entries.get(key);
    }
    public void delete(byte[] key) {
        validateKey(key);
        entries.remove(key);
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
}