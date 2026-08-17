    package io.github.mohul.observability.statistics;
    public class RuntimeStatistics {
        private long writeCount;
        private long readCount;
        private long deleteCount;
        private long flushCount;
        private final long startTime;
        private long totalReadTimeNanos;
        private long totalWriteTimeNanos;
        private long fastestReadTimeNanos = Long.MAX_VALUE;
        private long slowestReadTimeNanos;
        private long fastestWriteTimeNanos = Long.MAX_VALUE;
        private long slowestWriteTimeNanos;
        private long compactionCount;
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
        public void recordWriteTime(long nanos){
            totalWriteTimeNanos += nanos;

            if(nanos < fastestWriteTimeNanos){
                fastestWriteTimeNanos = nanos;
            }

            if(nanos > slowestWriteTimeNanos){
                slowestWriteTimeNanos = nanos;
            }
        }
        public void recordReadTime(long nanos){
            totalReadTimeNanos += nanos;

            if(nanos < fastestReadTimeNanos){
                fastestReadTimeNanos = nanos;
            }

            if(nanos > slowestReadTimeNanos){
                slowestReadTimeNanos = nanos;
            }
        }
        public long getTotalReadTimeNanos(){
            return totalReadTimeNanos;
        }

        public long getTotalWriteTimeNanos(){
            return totalWriteTimeNanos;
        }

        public long getFastestReadTimeNanos(){
            return fastestReadTimeNanos == Long.MAX_VALUE ? 0 : fastestReadTimeNanos;
        }

        public long getSlowestReadTimeNanos(){
            return slowestReadTimeNanos;
        }

        public long getFastestWriteTimeNanos(){
            return fastestWriteTimeNanos == Long.MAX_VALUE ? 0 : fastestWriteTimeNanos;
        }

        public long getSlowestWriteTimeNanos(){
            return slowestWriteTimeNanos;
        }
        public long getAverageReadTimeNanos(){
            if(readCount==0){
                return 0;
            }
            return totalReadTimeNanos/readCount;
        }

        public long getAverageWriteTimeNanos(){
            if(writeCount==0){
                return 0;
            }
            return totalWriteTimeNanos/writeCount;
        }
        public void incrementCompactionCount() {
            compactionCount++;
        }
        public long getCompactionCount() {
            return compactionCount;
        }
    }