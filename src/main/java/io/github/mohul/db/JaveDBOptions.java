package io.github.mohul.db;

public final class JaveDBOptions{
    private long memTableMaxSizeBytes = 64L*1024*1024;
    private boolean autoFlushEnabled = true;
    public long getMemTableMaxSizeBytes(){
        return memTableMaxSizeBytes;
    }
    public void setMemTableMaxSizeBytes(long memTableMaxSizeBytes){
        if(memTableMaxSizeBytes<=0){
            throw new IllegalArgumentException("MemTable size must be greater than zero.");
        }
        this.memTableMaxSizeBytes=memTableMaxSizeBytes;
    }
    public boolean isAutoFlushEnabled(){
        return autoFlushEnabled;
    }
    public void setAutoFlushEnabled(boolean autoFlushEnabled){
        this.autoFlushEnabled = autoFlushEnabled;
    }
}
