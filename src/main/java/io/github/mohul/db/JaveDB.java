package io.github.mohul.db;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import io.github.mohul.memtable.Entry;
import io.github.mohul.memtable.MemTable;
import io.github.mohul.wal.OperationType;
import io.github.mohul.wal.WALManager;
import io.github.mohul.wal.WALRecord;
public final class JaveDB {
    private final Path databasePath;
    private final MemTable memTable;
    private final WALManager walManager;
    public JaveDB(String databasePath) throws IOException {
        if (databasePath == null || databasePath.isBlank()) {
            throw new IllegalArgumentException("Database path cannot be null or empty.");
        }
        this.databasePath = Path.of(databasePath);
        Files.createDirectories(this.databasePath);
        this.memTable = new MemTable();
        this.walManager = new WALManager(this.databasePath.resolve("wal.log"));
        for (WALRecord record : walManager.replay()) {
            if (record.getOperation() == OperationType.PUT) {
                memTable.put(new Entry(record.getKey(), record.getValue()));
            } else {
                memTable.delete(record.getKey());
            }
        }
    }
    public void put(byte[] key, byte[] value) throws IOException {
        validateKey(key);
        validateValue(value);
        WALRecord record = new WALRecord(
                OperationType.PUT,
                key,
                value);
        walManager.append(record);
        Entry entry = new Entry(key, value);
        memTable.put(entry);
    }
    public byte[] get(byte[] key) {
        validateKey(key);
        Entry entry = memTable.get(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
    public void delete(byte[] key) throws IOException {
        validateKey(key);
        WALRecord record = new WALRecord(
                OperationType.DELETE,
                key,
                null);
        walManager.append(record);
        memTable.delete(key);
    }
    public void close() throws IOException {
        walManager.close();
    }
    private void validateKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("Key cannot be empty.");
        }
    }
    private void validateValue(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
    }
}