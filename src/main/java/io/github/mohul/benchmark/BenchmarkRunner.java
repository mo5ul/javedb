package io.github.mohul.benchmark;
import io.github.mohul.db.JaveDB;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
public final class BenchmarkRunner {
    private final JaveDB database;
    public BenchmarkRunner(JaveDB database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null.");
        }
        this.database = database;
    }
    public BenchmarkResult runPutBenchmark(int operations) throws IOException {
        long start = System.nanoTime();
        for (int i = 1; i <= operations; i++) {
            database.put(
                    ("bench-key-" + i).getBytes(StandardCharsets.UTF_8),
                    ("value-" + i).getBytes(StandardCharsets.UTF_8)
            );
        }
        long end = System.nanoTime();
        return new BenchmarkResult(
                "PUT Benchmark",
                operations,
                end - start
        );
    }
    public BenchmarkResult runGetBenchmark(int operations) throws IOException {
        long start = System.nanoTime();
        for (int i = 1; i <= operations; i++) {
            database.get(
                    ("bench-key-" + i).getBytes(StandardCharsets.UTF_8)
            );
        }
        long end = System.nanoTime();
        return new BenchmarkResult(
                "GET Benchmark",
                operations,
                end - start
        );
    }
}