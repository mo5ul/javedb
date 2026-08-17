package io.github.mohul.benchmark;
import io.github.mohul.db.JaveDB;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
public final class StressTester {
    private final JaveDB database;
    private final Random random = new Random(42);
    public StressTester(JaveDB database){
        this.database = database;
    }
    public BenchmarkResult run(int operations) throws IOException{
        long start = System.nanoTime();
        for(int i=1; i<=operations; i++){
            int action = random.nextInt(100);
            String key = "stress-key-"+random.nextInt(operations/2+1);
            if(action<50){
                database.put(key.getBytes(StandardCharsets.UTF_8),("value-"+i).getBytes(StandardCharsets.UTF_8));
            }else if(action<80){
                database.get(key.getBytes(StandardCharsets.UTF_8));
            } else {
                database.delete(key.getBytes(StandardCharsets.UTF_8));
            }
        }
        long end = System.nanoTime();
        return new BenchmarkResult("Stress Test", operations, end - start);
    }
}
