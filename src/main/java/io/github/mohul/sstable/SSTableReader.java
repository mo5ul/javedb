package io.github.mohul.sstable;
import io.github.mohul.util.ByteArrayComparator;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public final class SSTableReader {
    private static final int MAGIC_NUMBER = 0x4A444253;
    private static final int FOOTER_MAGIC = 0x464F4F54;
    private static final ByteArrayComparator COMPARATOR = new ByteArrayComparator();
    private final Path sstablePath;
    public SSTableReader(Path sstablePath) {
        if (sstablePath == null) {
            throw new IllegalArgumentException("SSTable path cannot be null.");
        }
        this.sstablePath = sstablePath;
    }
    public byte[] get(byte[] key) throws IOException{
        Record record = getRecord(key);
        if(record == null || record.isTombstone()){
            return null;
        }
        return record.getValue();
    }
    public int getEntryCount() throws IOException {
        try(RandomAccessFile file=new RandomAccessFile(sstablePath.toFile(),"r")){
            if(file.length()<24){
                throw new IOException("Invalid SSTable.");
            }
            int magic=file.readInt();
            if(magic!=MAGIC_NUMBER){
                throw new IOException("Invalid SSTable.");
            }
            file.readInt();
            return file.readInt();
        }


    }
        public static final class Record {
        private final byte[] key;
        private final byte[] value;
        private final boolean tombstone;
        public Record(byte[] key, byte[] value, boolean tombstone) {
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
    public List<Record> readAll() throws IOException {
        List<Record> records = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(sstablePath.toFile(), "r")) {
            int magic = file.readInt();
            if (magic != MAGIC_NUMBER) {
                throw new IOException("Invalid SSTable.");
            }
            file.readInt(); // version
            int entryCount = file.readInt();
            for (int i = 0; i < entryCount; i++) {
                byte flags = file.readByte();
                int keyLength = file.readInt();
                int valueLength = file.readInt();
                byte[] key = new byte[keyLength];
                file.readFully(key);
                byte[] value = null;
                if ((flags & 0x01) == 0) {
                    value = new byte[valueLength];
                    file.readFully(value);
                }
                boolean tombstone = (flags & 0x01) != 0;
                records.add(new Record(key, value, tombstone));
            }
        }
        return records;
    }
    public Record getRecord(byte[] key) throws IOException{
        if(key == null){
            throw new IllegalArgumentException("Key cannot be null.");
        }
        try (RandomAccessFile file = new RandomAccessFile(sstablePath.toFile(), "r")){
            long fileSize = file.length();
            if(fileSize<24){
                throw new IOException("Invalid SSTable.");
            }
            file.seek(fileSize -24);
            long indexOffset = file.readLong();
            long bloomOffset = file.readLong();
            int indexCount = file.readInt();
            int footerMagic = file.readInt();
            if (footerMagic != FOOTER_MAGIC){
                throw new IOException("Invalid SSTable footer.");
            }
            file.seek(bloomOffset);
            int bloomLength = file.readInt();
            byte[] bloomBytes = new byte[bloomLength];
            file.readFully(bloomBytes);
            BloomFilter bloomFilter = new BloomFilter();
            bloomFilter.load(bloomBytes);
            if(!bloomFilter.mightContain(key)){
                return null;
            }
            file.seek(indexOffset);
            List<IndexEntry> index = new ArrayList<>();
            for (int i = 0; i < indexCount; i++) {
                int keyLength = file.readInt();
                byte[] indexKey = new byte[keyLength];
                file.readFully(indexKey);
                long recordOffset = file.readLong();
                index.add(new IndexEntry(indexKey, recordOffset));
            }
            int left = 0;
            int right = index.size() - 1;
            long blockOffset = -1;
            while (left <= right) {
                int mid = (left + right) / 2;
                IndexEntry entry = index.get(mid);
                int cmp = COMPARATOR.compare(entry.getKey(), key);
                if (cmp == 0) {
                    blockOffset = entry.getOffset();
                    break;
                }
                if (cmp < 0) {
                    blockOffset = entry.getOffset();
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            if (blockOffset == -1) {
                return null;
            }
            file.seek(blockOffset);
            for (int i = 0; i < 16 && file.getFilePointer() < indexOffset; i++) {
                byte flags = file.readByte();
                int keyLength = file.readInt();
                int valueLength = file.readInt();
                byte[] recordKey = new byte[keyLength];
                file.readFully(recordKey);
                boolean tombstone = (flags & 0x01) != 0;
                if (COMPARATOR.compare(recordKey, key) == 0) {
                    if (tombstone) {
                        return new Record(recordKey, null, true);
                    }
                    byte[] value = new byte[valueLength];
                    file.readFully(value);
                    return new Record(recordKey, value, false);
                }
                if (!tombstone) {
                    file.skipBytes(valueLength);
                }
            }
            return null;
        }
    }
}