package io.github.mohul.memtable;
public final class Entry {
    private final byte[] key;
    private final byte[] value;
    private final boolean tombstone;
    public Entry(byte[] key, byte[] value) {
        this(key, value, false);
    }
    public Entry(byte[] key, byte[] value, boolean tombstone) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("Key cannot be empty.");
        }
        if (!tombstone && value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        this.key = key.clone();
        this.value = value == null ? null : value.clone();
        this.tombstone = tombstone;
    }
    public byte[] getKey() {
        return key.clone();
    }
    public byte[] getValue() {
        return value == null ? null : value.clone();
    }
    public boolean isTombstone() {
        return tombstone;
    }
}