package io.github.mohul.db;
import io.github.mohul.memtable.Entry;
import io.github.mohul.memtable.MemTable;
import io.github.mohul.sstable.SSTableWriter;
import io.github.mohul.sstable.SSTableReader;
import io.github.mohul.wal.OperationType;
import io.github.mohul.wal.WALManager;
import io.github.mohul.wal.WALRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Stream;
public final class JaveDB {
    private final Path databasePath;
    private MemTable memTable;
    private final WALManager walManager;
    private final List<Path> sstablePaths = new ArrayList<>();
    private int nextSSTableId = 1;
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
        try (Stream<Path> stream = Files.list(this.databasePath)){
            stream.filter(path -> path.getFileName().toString().endsWith(".jdbs")).sorted(Comparator.comparing(path -> path.getFileName().toString())).forEach(sstablePaths::add);
        }
        if(!sstablePaths.isEmpty()){
            Path lastSSTable = sstablePaths.get(sstablePaths.size()-1);
            String fileName = lastSSTable.getFileName().toString();
            String id = fileName.substring(0,fileName.indexOf('.'));
            nextSSTableId=Integer.parseInt(id) + 1;
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
    public byte[] get(byte[] key) throws IOException {
    validateKey(key);
    Entry entry = memTable.get(key);
    if (entry != null) {
        return entry.getValue();
    }
    for (int i = sstablePaths.size() - 1; i >= 0; i--) {
    SSTableReader reader = new SSTableReader(sstablePaths.get(i));
    byte[] value = reader.get(key);
    if (value != null) {
        return value;
    }
}
return null;
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
        if (value.length == 0) {
            throw new IllegalArgumentException("Value cannot be empty.");
        }
    }
    public void flush() throws IOException {
    Path sstablePath = databasePath.resolve(
            String.format("%06d.jdbs", nextSSTableId++)
    );
    SSTableWriter writer = new SSTableWriter(sstablePath);
    writer.write(memTable);
    sstablePaths.add(sstablePath);
    walManager.reset();
    memTable = new MemTable();
}
}