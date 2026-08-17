package io.github.mohul.benchmark;

import io.github.mohul.db.JaveDB;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PerformanceSuite {

    private final JaveDB database;

    public PerformanceSuite(JaveDB database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null.");
        }
        this.database = database;
    }

    public Path runAll() throws IOException {

        List<BenchmarkResult> results = new ArrayList<>();

        System.out.println("\nRunning PUT Benchmark...");
        BenchmarkRunner runner = new BenchmarkRunner(database);
        BenchmarkResult put = runner.runPutBenchmark(10000);
        results.add(put);
        System.out.println(put);

        System.out.println("\nRunning GET Benchmark...");
        BenchmarkResult get = runner.runGetBenchmark(10000);
        results.add(get);
        System.out.println(get);

        System.out.println("\nRunning Stress Test...");
        StressTester stress = new StressTester(database);
        BenchmarkResult stressResult = stress.run(25000);
        results.add(stressResult);
        System.out.println(stressResult);

        System.out.println("\nRunning Large Dataset Test...");
        LargeDatasetTester large = new LargeDatasetTester(database);
        BenchmarkResult largeResult = large.run(50000);
        results.add(largeResult);
        System.out.println(largeResult);

        System.out.println("\nRunning Reliability Test...");
        ReliabilityTester reliability =
                new ReliabilityTester(database.getDatabasePath());

        BenchmarkResult reliabilityResult = reliability.run(5000);
        results.add(reliabilityResult);
        System.out.println(reliabilityResult);
        database.flush();
        database.compact();
        return ReportGenerator.generateReport(database, results);
    }
}