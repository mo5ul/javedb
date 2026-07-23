package io.github.mohul.observability.storage;
public final class EntryInfo {
    private final byte[] key;
    private final byte[] value;
    public EntryInfo(byte[] key,byte[] value){
        this.key=key;
        this.value=value;
    }
    public byte[] getKey(){
        return key;
    }
    public byte[] getValue(){
        return value;
    }
}