package io.github.mohul.memtable;
public final class Entry{
    private final byte[] key;
    private final byte[] value;
    public Entry(byte[] key, byte[] value){
    if(key==null){
        throw new IllegalArgumentException("Key cannot be null.");
    }
    if(key.length==0){
        throw new IllegalArgumentException("Key cannot be empty.");
    }
    if(value==null){
        throw new IllegalArgumentException("Value cannot be null.");
    }
    this.key=key.clone();
    this.value=value.clone();
}
public byte[] getKey(){
    return key.clone();
}
public byte[] getValue(){
    return value.clone();
}
}