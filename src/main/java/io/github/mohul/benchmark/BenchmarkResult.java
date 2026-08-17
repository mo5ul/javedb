package io.github.mohul.benchmark;

public final class BenchmarkResult {
    private final String benchmarkName;
    private final long operations;
    private final long elapsedNanos;
    public BenchmarkResult(String benchmarkName, long operations, long elapsedNanos){
        this.benchmarkName = benchmarkName;
        this.operations = operations;
        this.elapsedNanos = elapsedNanos;
    }
    public String getBenchmarkName(){
        return benchmarkName;
    }
    public long getOperations(){
        return operations;
    }
    public long getElapsedNanos(){
        return elapsedNanos;
    }
    public double getElapsedSeconds(){
        return elapsedNanos / 1_000_000_000.0;
    }
    public double getThroughputOpsPerSecond(){
        return operations / getElapsedSeconds();
    }
    public double getAverageLatencyMicros(){
        return(elapsedNanos / 1000.0)/operations;
    }
    @Override
    public String toString(){
        return String.format("%s:%,d ops | %.2f sec | %.2f ops/sec | %.2f µs/op", benchmarkName, operations, getElapsedSeconds(), getThroughputOpsPerSecond(), getAverageLatencyMicros());
    }
}
