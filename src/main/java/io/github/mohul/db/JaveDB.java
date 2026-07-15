package io.github.mohul.db;
import java.nio.file.Path;
import io.github.mohul.memtable.Entry;
import io.github.mohul.memtable.MemTable;
public class JaveDB {
    private final Path databasePath;
    private final MemTable memTable;
    public JaveDB(String databasePath){
        if (databasePath == null || databasePath.isBlank()){
            throw new IllegalArgumentException("Database path cannot be null or empty.");
        }
        this.databasePath=Path.of(databasePath);
        this.memTable = new MemTable();
    }
    public void put(byte[] key, byte[] value){
        validateKey(key);
        validateValue(value);

        Entry entry = new Entry(key, value);
        memTable.put(entry);
    }
    public byte[] get(byte[] key){
        validateKey(key);

        Entry entry = memTable.get(key);
        if (entry==null){
            return null;
        }
        return entry.getValue();
    }
    public void delete(byte[] key){
        validateKey(key);

        memTable.delete(key);
    }
    public void close(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    private void validateKey(byte[] key){
        if(key==null){
            throw new IllegalArgumentException("Key cannot be empty.");
        }
        if (key.length ==0){
            throw new IllegalArgumentException("Key cannot be empty.");
        }
    }
    private void validateValue(byte[] value){
        if(value==null){
            throw new IllegalArgumentException("Value cannot be null");
        }
    }
}
