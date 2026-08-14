package io.github.mohul.sstable;
import java.util.BitSet;
public final class BloomFilter {
    private static final int DEFAULT_SIZE= 8192;
    private static final int HASH_COUNT=3;
    private final BitSet bits = new BitSet(DEFAULT_SIZE);
    public void add(byte[] key){
        for(int i=0;i<HASH_COUNT;i++){
            bits.set(hash(key, i));
        }
    }
    public boolean mightContain(byte[] key){
        for(int i=0; i<HASH_COUNT; i++){
            if(!bits.get(hash(key, i))){
                return false;
            }
        }
        return true;
    }
    public void load(byte[] data){
        bits.clear();
        bits.or(BitSet.valueOf(data));
    }
    public byte[] toByteArray(){
        return bits.toByteArray();
    }
    private int hash(byte[] key,int seed){
        int h = seed*0x9E3779B9;
        for(byte b:key){
            h^=b;
            h*= 0x85EBCA6B;
        }
        return Math.floorMod(h, DEFAULT_SIZE);
    }
}
