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
    public byte[] get(byte[] key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        try (RandomAccessFile file = new RandomAccessFile(sstablePath.toFile(), "r")) {
            long fileSize = file.length();
            if(fileSize<16){
                throw new IOException("Invalid SSTable.");
            }
            file.seek(fileSize-16);
            long indexOffset = file.readLong();
            int indexCount = file.readInt();
            int footerMagic = file.readInt();
            if(footerMagic != FOOTER_MAGIC){
                throw new IOException("Invalid SSTable footer.");
            }
            file.seek(indexOffset);
            List<IndexEntry> index = new ArrayList<>();
            for(int i=0; i<indexCount; i++){
                int keyLength = file.readInt();
                byte[] indexKey = new byte[keyLength];
                file.readFully(indexKey);
                long recordOffset = file.readLong();
                index.add(new IndexEntry(indexKey, recordOffset));
            }
            long recordOffset = -1;
            for(IndexEntry entry : index){
                if (COMPARATOR.compare(entry.getKey(), key)==0){
                    recordOffset = entry.getOffset();
                    break;
                }
            }
            if(recordOffset==-1){
                return null;
            }
            file.seek(recordOffset);
            byte flags = file.readByte();
            int keyLength = file.readInt();
            int valueLength = file.readInt();
            byte[] recordKey = new byte[keyLength];
            file.readFully(recordKey);
            byte[] value = new byte[valueLength];
            file.readFully(value);
            if (COMPARATOR.compare(recordKey,key)!=0){
                return null;
            }
            if((flags&0x01)!=0){
                return null;
            }
            return value;
        }
    }
    public int getEntryCount() throws IOException {
        try(RandomAccessFile file=new RandomAccessFile(sstablePath.toFile(),"r")){
            if(file.length()<12){
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
}