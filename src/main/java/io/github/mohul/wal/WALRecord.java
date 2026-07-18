package io.github.mohul.wal;
public final class WALRecord {
    private final OperationType operation;
    private final byte[] key;
    private final byte[] value;
    public WALRecord(OperationType operation, byte[] key, byte[] value) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("Key cannot be empty.");
        }
        if (operation == OperationType.PUT && value == null) {
            throw new IllegalArgumentException("Value cannot be null for PUT.");
        }
        if (operation == OperationType.DELETE && value != null) {
            throw new IllegalArgumentException("DELETE operation cannot have a value.");
        }
        this.operation = operation;
        this.key = key.clone();
        this.value = (value == null) ? null : value.clone();
    }
    public OperationType getOperation() {
        return operation;
    }
    public byte[] getKey() {
        return key.clone();
    }
    public byte[] getValue() {
        return value == null ? null : value.clone();
    }
}