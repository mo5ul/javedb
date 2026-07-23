package io.github.mohul.observability.storage;
import io.github.mohul.wal.OperationType;
public final class WALRecordInfo {
    private final OperationType operation;
    private final byte[] key;
    private final byte[] value;
    public WALRecordInfo(OperationType operation,byte[] key,byte[] value){
        this.operation=operation;
        this.key=key;
        this.value=value;
    }
    public OperationType getOperation(){
        return operation;
    }
    public byte[] getKey(){
        return key;
    }
    public byte[] getValue(){
        return value;
    }
}