package io.github.mohul.db;
import java.nio.file.Path;

public class JaveDB {
    private final Path databasePath;
    public JaveDB(String databasePath){
        if (databasePath == null || databasePath.isBlank()){
            throw new IllegalArgumentException("Database path cannot be null or empty.");
        }
        this.databasePath=Path.of(databasePath);
    }
    public void put(byte[] key, byte[] value){
        validateKey(key);
        validateValue(value);

        throw new UnsupportedOperationException("Not implemented yet.");
    }
    public byte[] get(byte[] key){
        validateKey(key);

        throw new UnsupportedOperationException("Not implemented yet.");
    }
    public void delete(byte[] key){
        validateKey(key);

        throw new UnsupportedOperationException("Not implemented yet.");
    }
    public void close(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    private void validateKey(byte[] key){
        if(key==null){
            throw new IllegalArgumentException("Key cannot be empty.");
        }
    }
    private void validateValue(byte[] value){
        if(value==null){
            throw new IllegalArgumentException("Value cannot be null");
        }
    }
}
