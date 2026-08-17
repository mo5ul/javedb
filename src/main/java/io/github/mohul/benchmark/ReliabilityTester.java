package io.github.mohul.benchmark;
import io.github.mohul.db.JaveDB;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
public class ReliabilityTester {
    private final Path databasePath;
    public ReliabilityTester(Path databasePath){
        if(databasePath == null){
            throw new IllegalArgumentException("Database path cannot be null.");
        }
        this.databasePath = databasePath;
    }
    public BenchmarkResult run(int records) throws IOException{
        long start = System.nanoTime();
        JaveDB db = new JaveDB(databasePath.toString()); 
        for(int i = 1; i<=records; i++){
            db.put(("recovery-key-"+i).getBytes(StandardCharsets.UTF_8),("value-"+i).getBytes(StandardCharsets.UTF_8));
        }
        db.flush();
        db = new JaveDB(databasePath.toString());
        for (int i=1; i<=records; i++){
            byte[] value = db.get(("recovery-key-"+i).getBytes(StandardCharsets.UTF_8));
            String expected = "value-"+i;
            if(value==null || !expected.equals(new String(value,StandardCharsets.UTF_8))){
                throw new IOException("Recovery verification failed for key"+i);
            }
        }
        long end = System.nanoTime();
        return new BenchmarkResult("Reliability Test",records, end-start);
    }
}
