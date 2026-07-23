package io.github.mohul.db;
import io.github.mohul.memtable.Entry;
import io.github.mohul.memtable.MemTable;
import io.github.mohul.metadata.MetadataConstants;
import io.github.mohul.metadata.MetadataReader;
import io.github.mohul.metadata.MetadataWriter;
import io.github.mohul.sstable.SSTableReader;
import io.github.mohul.sstable.SSTableWriter;
import io.github.mohul.wal.OperationType;
import io.github.mohul.wal.WALManager;
import io.github.mohul.wal.WALRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import io.github.mohul.observability.event.EngineEventLog;
import io.github.mohul.observability.health.HealthReport;
import io.github.mohul.observability.health.HealthStatus;
import io.github.mohul.observability.info.DatabaseConfiguration;
import io.github.mohul.observability.info.DatabaseInfo;
import io.github.mohul.observability.info.DatabaseSummary;
import io.github.mohul.observability.statistics.RuntimeStatistics;
import io.github.mohul.observability.statistics.StorageStatistics;
import io.github.mohul.observability.storage.EntryInfo;
import io.github.mohul.observability.storage.MemTableInfo;
import io.github.mohul.observability.storage.SSTableInfo;
import io.github.mohul.observability.storage.WALInfo;
import io.github.mohul.observability.storage.WALRecordInfo;
public final class JaveDB {
    private final Path databasePath;
    private MemTable memTable;
    private final WALManager walManager;
    private final List<Path> sstablePaths = new ArrayList<>();
    private final JaveDBOptions options;
    private int nextSSTableId = 1;
    private final RuntimeStatistics runtimeStatistics;
    private final DatabaseInfo databaseInfo;
    private final EngineEventLog eventLog;
    private StorageStatistics storageStatistics;
    private final long openedAt;
    private boolean open;   
    public JaveDB(String databasePath) throws IOException {
        this(databasePath, new JaveDBOptions());
    }
    public JaveDB(String databasePath, JaveDBOptions options) throws IOException {
        if (databasePath == null || databasePath.isBlank()) {
            throw new IllegalArgumentException("Database path cannot be null or empty.");
        }
        this.options = Objects.requireNonNull(options, "Options cannot be null.");
        this.databasePath = Path.of(databasePath);
        Files.createDirectories(this.databasePath);
        Path metadataPath = this.databasePath.resolve(MetadataConstants.METADATA_FILE_NAME);
        if (Files.exists(metadataPath)) {
            try (MetadataReader reader = new MetadataReader(metadataPath)) {
                this.databaseInfo = reader.read();
            }
        } else {
            this.databaseInfo = new DatabaseInfo(this.databasePath.getFileName().toString(), UUID.randomUUID().toString(), System.currentTimeMillis());
            try (MetadataWriter writer = new MetadataWriter(metadataPath)) {
                writer.write(this.databaseInfo);
            }
        }
        this.memTable = new MemTable();
        this.runtimeStatistics = new RuntimeStatistics();
        this.eventLog = new EngineEventLog();
        this.openedAt = System.currentTimeMillis();
        this.open = true;
        this.eventLog.addEvent("DATABASE", "Database opened.");
        this.walManager = new WALManager(this.databasePath.resolve("wal.log"));
        for (WALRecord record : walManager.replay()) {
            if (record.getOperation() == OperationType.PUT) {
                memTable.put(new Entry(record.getKey(), record.getValue()));
            } else {
                memTable.delete(record.getKey());
            }
        }
        try (Stream<Path> stream = Files.list(this.databasePath)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".jdbs")).sorted(Comparator.comparing(path -> path.getFileName().toString())).forEach(sstablePaths::add);
        }
        if (!sstablePaths.isEmpty()) {
            Path lastSSTable = sstablePaths.get(sstablePaths.size() - 1);
            String fileName = lastSSTable.getFileName().toString();
            String id = fileName.substring(0, fileName.indexOf('.'));
            nextSSTableId = Integer.parseInt(id) + 1;
        }
        updateStorageStatistics();
    }
    public void put(byte[] key, byte[] value) throws IOException {
        validateKey(key);
        validateValue(value);
        WALRecord record = new WALRecord(OperationType.PUT, key, value);
        walManager.append(record);
        Entry entry = new Entry(key, value);
        memTable.put(entry);
        runtimeStatistics.incrementWriteCount();
        updateStorageStatistics();
        eventLog.addEvent("PUT", "Key inserted.");
        if (options.isAutoFlushEnabled() && memTable.getEstimatedSizeInBytes() >= options.getMemTableMaxSizeBytes()) {
            flush();
        }
    }
    public byte[] get(byte[] key) throws IOException {
        validateKey(key);
        runtimeStatistics.incrementReadCount();
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
        runtimeStatistics.incrementDeleteCount();
        WALRecord record = new WALRecord(OperationType.DELETE, key, null);
        walManager.append(record);
        memTable.delete(key);
        updateStorageStatistics();
        eventLog.addEvent("DELETE", "Key deleted.");
    }
    public void flush() throws IOException {
        Path sstablePath = databasePath.resolve(String.format("%06d.jdbs", nextSSTableId++));
        SSTableWriter writer = new SSTableWriter(sstablePath);
        writer.write(memTable);
        sstablePaths.add(sstablePath);
        walManager.reset();
        memTable = new MemTable();
        runtimeStatistics.incrementFlushCount();
        updateStorageStatistics();
        eventLog.addEvent("FLUSH", "MemTable flushed to SSTable.");
    }
    public void close() throws IOException {
        if (!open) {
            return;
        }
        eventLog.addEvent("DATABASE", "Database closed.");
        walManager.close();
        open = false;
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
    private void updateStorageStatistics() throws IOException {
        long totalSSTableSizeBytes = 0;
        for (Path path : sstablePaths) {
            totalSSTableSizeBytes += Files.size(path);
        }
        storageStatistics = new StorageStatistics(memTable.getEstimatedSizeInBytes(), memTable.size(), sstablePaths.size(), totalSSTableSizeBytes);
    }
    public RuntimeStatistics getRuntimeStatistics() {
        return runtimeStatistics;
    }
    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }
    public StorageStatistics getStorageStatistics() {
        return storageStatistics;
    }
    public EngineEventLog getEventLog() {
        return eventLog;
    }
    public HealthReport getHealthReport() {
        if (!Files.exists(databasePath)) {
            return new HealthReport(HealthStatus.ERROR, "Database directory does not exist.");
        }
        if (!Files.exists(databasePath.resolve(MetadataConstants.METADATA_FILE_NAME))) {
            return new HealthReport(HealthStatus.ERROR, "Database metadata is missing.");
        }
        if (memTable.getEstimatedSizeInBytes() > options.getMemTableMaxSizeBytes()) {
            return new HealthReport(HealthStatus.WARNING, "MemTable size exceeds configured limit.");
        }
        return new HealthReport(HealthStatus.HEALTHY, "Database is healthy.");
    }
    public Path getDatabasePath() {
        return databasePath;
    }
    public List<SSTableInfo> getSSTableInfo() throws IOException {
        List<SSTableInfo> tables=new ArrayList<>();
        for(Path path:sstablePaths){
            SSTableReader reader=new SSTableReader(path);
    tables.add(new SSTableInfo(
        path.getFileName().toString(),
        path,
        Files.size(path),
        reader.getEntryCount(),
        Files.getLastModifiedTime(path).toMillis()));
    }
    return List.copyOf(tables);
}
    public MemTableInfo getMemTableInfo() {
    return new MemTableInfo(memTable.size(),memTable.getEstimatedSizeInBytes());
}
    public DatabaseConfiguration getConfiguration() {
        return new DatabaseConfiguration(databasePath,options.getMemTableMaxSizeBytes(),options.isAutoFlushEnabled());
    }
    public int getNextSSTableId() {
        return nextSSTableId;
    }
    public WALInfo getWALInfo() throws IOException {
        Path walPath=databasePath.resolve("wal.log");
        boolean exists=Files.exists(walPath);
        long size=exists?Files.size(walPath):0;
        return new WALInfo(walPath,exists,size);
    }
    public boolean isOpen() {
        return open;
    }
    public long getOpenedAt() {
        return openedAt;
    }
    public DatabaseSummary getSummary() throws IOException {
        return new DatabaseSummary(
            databaseInfo,
            runtimeStatistics,
            storageStatistics,
            getMemTableInfo(),
            getWALInfo(),
            getConfiguration(),
            getHealthReport(),
            open,
            openedAt
        );
    }
    public long getDatabaseSize() throws IOException {
        long size=0;
        try(Stream<Path> stream=Files.walk(databasePath)){
            for(Path path:stream.filter(Files::isRegularFile).toList()){
                size+=Files.size(path);
            }
        }
        return size;
    }
    public List<EntryInfo> getMemTableEntries() {
        List<EntryInfo> entries=new ArrayList<>();
        for(Entry entry:memTable.entries()){
            entries.add(new EntryInfo(entry.getKey(),entry.getValue()));
        }
        return List.copyOf(entries);
    }
    public List<WALRecordInfo> getWALRecords() throws IOException {
        List<WALRecordInfo> records=new ArrayList<>();
        for(WALRecord record:walManager.replay()){
            records.add(new WALRecordInfo(record.getOperation(),record.getKey(),record.getValue()));
        }
        return List.copyOf(records);
    }
    public void clearEventLog() {
        eventLog.clear();
    }
    public int getTotalEntries() throws IOException {
        int total=memTable.size();
        for(Path path:sstablePaths){
            total+=new SSTableReader(path).getEntryCount();
        }
        return total;
    }
    public void setAutoFlushEnabled(boolean enabled){
        options.setAutoFlushEnabled(enabled);
        eventLog.addEvent("SETTINGS","Auto Flush "+(enabled?"enabled.":"disabled."));
    }
    public void setMemTableMaxSizeBytes(long size) throws IOException{
        options.setMemTableMaxSizeBytes(size);
        eventLog.addEvent("SETTINGS","MemTable limit changed to "+size+" bytes.");
        if(memTable.getEstimatedSizeInBytes()>=options.getMemTableMaxSizeBytes()){
            flush();
        }
    }
}