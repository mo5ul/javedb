package io.github.mohul.observability.storage;
import java.nio.file.Path;
public final class SSTableInfo {
    private final String fileName;
    private final Path path;
    private final long sizeBytes;
    private final int entryCount;
    private final long createdTime;
    public SSTableInfo(String fileName,Path path,long sizeBytes,int entryCount,long createdTime){
        this.fileName=fileName;
        this.path=path;
        this.sizeBytes=sizeBytes;
        this.entryCount=entryCount;
        this.createdTime=createdTime;
    }
    public String getFileName() {
        return fileName;
    }
    public Path getPath() {
        return path;
    }
    public long getSizeBytes() {
        return sizeBytes;
    }
    public int getEntryCount(){
        return entryCount;
    }
    public long getCreatedTime(){
        return createdTime;
    }
}