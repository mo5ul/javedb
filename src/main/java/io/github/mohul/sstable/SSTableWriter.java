package io.github.mohul.sstable;
import io.github.mohul.memtable.Entry;
import io.github.mohul.memtable.MemTable;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public final class SSTableWriter {
    private static final int MAGIC_NUMBER = 0x4A444253; // "JDBS"
    private static final int VERSION = 1;
    private static final int SPARSE_INDEX_INTERVAL = 16;
    private static final int FOOTER_MAGIC = 0x464F4F54;    
    private final Path sstablePath;
    public SSTableWriter(Path sstablePath) {
        if (sstablePath == null) {
            throw new IllegalArgumentException("SSTable path cannot be null.");
        }
        this.sstablePath = sstablePath;
    }
    public void write(MemTable memTable) throws IOException {
        if (memTable == null) {
            throw new IllegalArgumentException("MemTable cannot be null.");
        }
        List<IndexEntry> indexEntries = new ArrayList<>();
        BloomFilter bloomFilter = new BloomFilter();
        try (CountingOutputStream countingOut = new CountingOutputStream(new BufferedOutputStream(Files.newOutputStream(sstablePath)));
        DataOutputStream out = new DataOutputStream(countingOut)) {
            out.writeInt(MAGIC_NUMBER);
            out.writeInt(VERSION);
            out.writeInt(memTable.size());
            int recordNumber = 0;
            for (Entry entry : memTable.entries()) {
                byte[] key = entry.getKey();
                bloomFilter.add(key);
                byte[] value = entry.getValue();
                long recordOffset = countingOut.getBytesWritten();
                if (recordNumber % SPARSE_INDEX_INTERVAL == 0) {
                    indexEntries.add(new IndexEntry(key, recordOffset));
                }
                if (entry.isTombstone()) {
                    out.writeByte(0x01);
                    out.writeInt(key.length);
                    out.writeInt(0);
                    out.write(key);
                } else {
                    out.writeByte(0x00);
                    out.writeInt(key.length);
                    out.writeInt(value.length);
                    out.write(key);
                    out.write(value);
                }
                recordNumber++;
            }
            long indexOffset = countingOut.getBytesWritten();
            for(IndexEntry indexEntry : indexEntries){
                byte[] key = indexEntry.getKey();
                out.writeInt(key.length);
                out.write(key);
                out.writeLong(indexEntry.getOffset());
            }
            long bloomOffset = countingOut.getBytesWritten();
            byte[] bloomBytes = bloomFilter.toByteArray();
            out.writeInt(bloomBytes.length);
            out.write(bloomBytes);
            out.writeLong(indexOffset);
            out.writeLong(bloomOffset);
            out.writeInt(indexEntries.size());
            out.writeInt(FOOTER_MAGIC);
        }
    }
}