package io.github.mohul.sstable;

public final class IndexEntry {
    private final byte[] key;
    private final long offset;
    public IndexEntry(byte[] key, long offset){
        if(key==null){
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.key=key;
        this.offset=offset;
    }
    public byte[] getKey(){
        return key;
    }
    public long getOffset(){
        return offset;
    }
}
