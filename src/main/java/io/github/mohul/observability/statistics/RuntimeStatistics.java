package io.github.mohul.observability.statistics;

public class RuntimeStatistics {
    private long writeCount;
    private long readCount;
    private long deleteCount;
    private long flushCount;
    private final long startTime;
    public RuntimeStatistics(){
        this.startTime = System.currentTimeMillis();
    }
    public long getWriteCount(){
        return writeCount;
    }
    public long getReadCount(){
        return readCount;
    }
    public long getDeleteCount(){
        return deleteCount;
    }
    public long getFlushCount(){
        return flushCount;
    }
    public long getStartTime(){
        return startTime;
    }
    public void incrementWriteCount(){
        writeCount++;
    }
    public void incrementReadCount(){
        readCount++;
    }
    public void incrementDeleteCount(){
        deleteCount++;
    }
    public void incrementFlushCount(){
        flushCount++;
    }
}
