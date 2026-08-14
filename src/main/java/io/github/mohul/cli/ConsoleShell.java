package io.github.mohul.cli;
import io.github.mohul.db.JaveDB;
import io.github.mohul.observability.event.EngineEvent;
import io.github.mohul.observability.info.DatabaseSummary;
import io.github.mohul.observability.statistics.RuntimeStatistics;
import io.github.mohul.observability.storage.CompactionResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import io.github.mohul.observability.storage.EntryInfo;
import io.github.mohul.observability.storage.MemTableInfo;
import io.github.mohul.observability.storage.SSTableInfo;
import io.github.mohul.observability.storage.WALInfo;
import io.github.mohul.observability.storage.WALRecordInfo;
import java.nio.file.Files;
import java.nio.file.Path;
public class ConsoleShell{
    private final Scanner scanner = new Scanner(System.in);
    private JaveDB database;
    public void start(){
        while(true){
            printHeader();
            printMenu();
            System.out.print("Select Option: ");
            String input = scanner.nextLine();
            switch(input){
                case "1":
                    databaseManager();
                    break;
                case "2":
                    dashboard();
                    break;
                case "3":
                    databaseOperations();
                    break;
                case "4":
                    storageExplorer();
                    break;
                case "5":
                    runtimeMonitor();
                    break;
                case "6":
                    healthMonitor();
                    break;
                case "7":
                    eventLog();
                    break;
                case "8":
                    settings();
                    break;
                case "9":
                    about();
                    break;
                case "10":
                    closeDatabase();
                    System.out.println();
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }
    private void printHeader(){
        System.out.println();
        System.out.println("====================================");
        System.out.println("           JaveDB Console");
        System.out.println("====================================");
    }
    private void printMenu(){
        System.out.println("1. Database Manager");
        System.out.println("2. Dashboard");
        System.out.println("3. Database Operations");
        System.out.println("4. Storage Explorer");
        System.out.println("5. Runtime Monitor");
        System.out.println("6. Health Monitor");
        System.out.println("7. Event Log");
        System.out.println("8. Settings");
        System.out.println("9. About");
        System.out.println("10. Exit");
        System.out.println();
    }
    private void databaseManager(){
        while(true){
            System.out.println();
            System.out.println("Database Manager");
            System.out.println("------------------------------------------------------------");

            String current="None";
            if(database!=null&&database.isOpen()){
                current=database.getDatabaseInfo().getDatabaseName();
            }
            System.out.println("Current Database : "+current);
            System.out.println();
            System.out.println("1. Create Database");
            System.out.println("2. Open Database");
            System.out.println("3. Close Database");
            System.out.println("4. List Databases");
            System.out.println("5. Delete Database");
            System.out.println("6. Back");
            System.out.println();
            System.out.print("Select Option: ");
            String option=scanner.nextLine().trim();
            switch(option){
                case "1":
                    createDatabase();
                    break;
                case "2":
                    openDatabase();
                    break;
                case "3":
                    closeCurrentDatabase();
                    break;
                case "4":
                    listDatabases();
                    break;
                case "5":
                    deleteDatabase();
                    break;
                case "6":
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }
    private void createDatabase(){
        try{
            if(database!=null&&database.isOpen()){
                System.out.println();
                System.out.println("A database is already open!");
                pause();
                return;
            }
            System.out.println();
            System.out.print("Database Name: ");
            String name=scanner.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("Database name cannot be empty.");
                pause();
                return;
            }
            Path path=Path.of(System.getProperty("user.home"),"Jave","DB",name);
            if(Files.exists(path)){
                System.out.println();
                System.out.println("Database already exists.");
                pause();
                return;
            }
            long memTableSizeBytes=promptMemTableSize();
            database=new JaveDB(path.toString());
            database.setMemTableMaxSizeBytes(memTableSizeBytes);
            System.out.println();
            System.out.println("Database created successfully!");
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to create database!");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private long promptMemTableSize(){
        while(true){
            System.out.println();
            System.out.println("MemTable Size (MB) [Default: 64]:");
            System.out.println();
            System.out.println("Press Enter to use the default value, or enter a custom size in MB.");
            System.out.print("> ");
            String input=scanner.nextLine().trim();
            if(input.isEmpty()){
                return 64L*1024L*1024L;
            }
            try{
                long size=Long.parseLong(input);
                if(size<=0){
                    System.out.println();
                    System.out.println("MemTable size must be greater than zero.");
                    continue;
                }
                return size*1024L*1024L;
            }catch(NumberFormatException e){
                System.out.println();
                System.out.println("Please enter a valid number.");
            }
        }
    }
    private void openDatabase(){
        try{
            if(database!=null&&database.isOpen()){
                System.out.println();
                System.out.println("A database is already open!");
                pause();
                return;
            }
            System.out.println();
            System.out.print("Database Name: ");
            String name=scanner.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("Database name cannot be empty.");
                pause();
                return;
            }
            Path path=Path.of(System.getProperty("user.home"),"Jave","DB",name);
            if(!Files.exists(path)){
                System.out.println();
                System.out.println("Database does not exist.");
                pause();
                return;
            }
            database=new JaveDB(path.toString());
            System.out.println();
            System.out.println("Database opened successfully!");
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to open database!");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void listDatabases(){
        try{
            Path root=Path.of(System.getProperty("user.home"),"Jave","DB");
            System.out.println();
            System.out.println("Available Databases");
            System.out.println("------------------------------------------------------------");
            if(!Files.exists(root)){
                System.out.println();
                System.out.println("No databases found.");
                pause();
                return;
            }
            List<Path> databases;
            try(Stream<Path> stream=Files.list(root)){
                databases=stream
                        .filter(Files::isDirectory)
                        .sorted(Comparator.comparing(path->path.getFileName().toString()))
                        .toList();
            }
            if(databases.isEmpty()){
                System.out.println();
                System.out.println("No databases found.");
            }else{
                int count=1;
                for(Path path:databases){
                    System.out.printf("%d. %s%n",count++,path.getFileName());
                }
            }
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to list databases.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void closeCurrentDatabase(){
        if(database==null||!database.isOpen()){
            System.out.println();
            System.out.println("No database is currently open.");
            pause();
            return;
        }
        try{
            database.close();
            database=null;
            System.out.println();
            System.out.println("Database closed successfully.");
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to close database.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void deleteDatabase(){
        try{
            if(database!=null&&database.isOpen()){
                System.out.println();
                System.out.println("Please close the currently open database first.");
                pause();
                return;
            }
            System.out.println();
            System.out.print("Database Name: ");
            String name=scanner.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("Database name cannot be empty.");
                pause();
                return;
            }
            Path path=Path.of(System.getProperty("user.home"),"Jave","DB",name);
            if(!Files.exists(path)){
                System.out.println();
                System.out.println("Database does not exist.");
                pause();
                return;
            }
            System.out.println();
            System.out.print("Are you sure (Y/N)? ");
            String answer=scanner.nextLine().trim();
            if(!answer.equalsIgnoreCase("Y")){
                System.out.println();
                System.out.println("Delete cancelled.");
                pause();
                return;
            }
            try(Stream<Path> stream=Files.walk(path)){
                stream.sorted(Comparator.reverseOrder())
                    .forEach(p->{
                        try{
                            Files.delete(p);
                        }catch(IOException e){
                            throw new RuntimeException(e);
                        }
                    });
            }
            System.out.println();
            System.out.println("Database deleted successfully.");
        }catch(RuntimeException e){
            System.out.println();
            System.out.println("Failed to delete database.");
            if(e.getCause()!=null){
                System.out.println(e.getCause().getMessage());
            }
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to delete database.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void dashboard(){
        if(!requireDatabase("Dashboard")){
            return;
        }
        try{
            DatabaseSummary summary = database.getSummary();
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println();
            System.out.println("Database Name       : " + summary.getDatabaseInfo().getDatabaseName());
            System.out.println("Database UUID       : " + summary.getDatabaseInfo().getDatabaseUuid());
            System.out.println("Created At          : " +formatTimestamp(summary.getDatabaseInfo().getCreatedAt()));
            System.out.println("Database Status     : " + (summary.isOpen() ? "OPEN":"CLOSED"));
            System.out.println("Opened At           : " +formatTimestamp(summary.getOpenedAt()));
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("HEALTH");
            System.out.println();
            System.out.println("Health           : " + summary.getHealthReport().getStatus());
            System.out.println("Health Message   : " + summary.getHealthReport().getMessage());
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("STORAGE");
            System.out.println();
            System.out.println("MemTable Entries    : "+summary.getStorageStatistics().getMemTableEntryCount());
            System.out.println("MemTable Size       : "+formatSize(summary.getStorageStatistics().getMemTableSizeBytes()));
            System.out.println("SSTable Count       : "+summary.getStorageStatistics().getSstableCount());
            System.out.println("SSTable Size        : "+formatSize(summary.getStorageStatistics().getTotalSSTableSizeBytes()));
            System.out.println("WAL Exists          : " + (summary.getWalInfo().exists() ? "Yes" : "No"));
            System.out.println("WAL Size            : "+formatSize(summary.getWalInfo().getSizeBytes()));
        }catch(IOException e){
            System.out.println();
            System.out.println("Failed to load dashboard!");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void databaseOperations(){
        if(!requireDatabase("Database Operations")){
            return;
        }
        while(true){
            System.out.println();
            System.out.println("Database Operations");
            System.out.println("------------------------------------------------------------");
            System.out.println("1. Put Entry");
            System.out.println("2. Get Entry");
            System.out.println("3. Delete Entry");
            System.out.println("4. Flush MemTable");
            System.out.println("5. Compact SSTables");
            System.out.println("6. Back");
            System.out.println();
            System.out.print("Select Option: ");
            String option = scanner.nextLine().trim();
            switch(option){
                case "1":
                    putEntry();
                    break;
                case "2":
                    getEntry();
                    break;
                case "3":
                    deleteEntry();
                    break;
                case "4":
                    flushMemTable();
                    break;
                case "5":
                    compactSSTables();
                    break;
                case "6":
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }
    private void putEntry(){
        try{
            System.out.println();
            System.out.println("Put Entry");
            System.out.println("------------------------------------------------------------");
            System.out.println("Key   :");
            String key = scanner.nextLine().trim();
            System.out.print("Value : ");
            String value = scanner.nextLine();
            database.put(key.getBytes(StandardCharsets.UTF_8),value.getBytes(StandardCharsets.UTF_8));
            System.out.println();
            System.out.println("Entry stored successfully!");
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to store entry");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void getEntry(){
        try{
            System.out.println();
            System.out.println("Get Entry");
            System.out.println("------------------------------------------------------------");
            System.out.print("Key : ");
            String key=scanner.nextLine().trim();
            byte[] value=database.get(key.getBytes(StandardCharsets.UTF_8));
            System.out.println();
            if(value==null){
                System.out.println("Entry not found.");
            }else{
                System.out.println("Value : "+new String(value,StandardCharsets.UTF_8));
            }
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to retrieve entry.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void deleteEntry(){
        try{
            System.out.println();
            System.out.println("Delete Entry");
            System.out.println("------------------------------------------------------------");
            System.out.print("Key : ");
            String key=scanner.nextLine().trim();

            boolean deleted=database.delete(key.getBytes(StandardCharsets.UTF_8));

            System.out.println();
            if(deleted){
                System.out.println("Entry deleted successfully.");
            }else{
                System.out.println("Key not found.");
            }
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to delete entry.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void flushMemTable(){
        try{
            System.out.println();
            System.out.println("Flush MemTable");
            System.out.println("------------------------------------------------------------");
            database.flush();
            System.out.println();
            System.out.println("MemTable flushed successfully!");
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to flush MemTable!");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void compactSSTables() {
        try {
            System.out.println();
            System.out.println("Compact SSTables");
            System.out.println("------------------------------------------------------------");
            CompactionResult result = database.compact();
            System.out.println();
            System.out.println("Compaction completed successfully!");
            System.out.println();
            System.out.printf("%-22s : %d%n",
                    "SSTables Merged",
                    result.getMergedSSTables());
            System.out.printf("%-22s : %s%n",
                    "New SSTable",
                    result.getNewSSTable());
            System.out.printf("%-22s : %d%n",
                    "Live Records",
                    result.getLiveRecords());
            System.out.printf("%-22s : %d%n",
                    "Tombstones Removed",
                    result.getTombstonesRemoved());
        } catch (Exception e) {
            System.out.println();
            System.out.println("Failed to compact SSTables!");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void storageExplorer(){
        if(!requireDatabase("Storage Explorer")){
            return;
        }while(true){
            System.out.println();
            System.out.println("Storage Explorer");
            System.out.println("------------------------------------------------------------");
            System.out.println("1. MemTable");
            System.out.println("2. SSTables");
            System.out.println("3. Write-Ahead Log (WAL)");
            System.out.println("4. Back");
            System.out.print("Select Option: ");
            String option = scanner.nextLine().trim();
            switch(option){
                case "1":
                    memTableExplorer();
                    break;
                case "2":
                    sstableExplorer();
                    break;
                case "3":
                    walExplorer();
                    break;
                case "4":
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }
    private void memTableExplorer(){
        try{
            MemTableInfo info = database.getMemTableInfo();
            System.out.println();
            System.out.println("MemTable Explorer");
            System.out.println("------------------------------------------------------------");
            System.out.println("Entries     : "+info.getEntryCount());
            System.out.println("Size        : "+info.getEstimatedSizeBytes()+" Bytes");
            if(info.getEntryCount()==0){
                System.out.println();
                System.out.println("MemTable is empty.");
            }else{
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.printf("%-30s %s%n","Key","Value");
                System.out.println("------------------------------------------------------------");
                for(EntryInfo entry:database.getMemTableEntries()){
                    String key=new String(entry.getKey(),StandardCharsets.UTF_8);
                    String value=new String(entry.getValue(),StandardCharsets.UTF_8);
                    System.out.printf("%-30s %s%n",key,value);
                }
            }
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to load MemTable.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void sstableExplorer(){
        try{
            List<SSTableInfo> tables=database.getSSTableInfo();
            System.out.println();
            System.out.println("SSTable Explorer");
            System.out.println("------------------------------------------------------------");
            System.out.println("Total SSTables : "+tables.size());
            if(tables.isEmpty()){
                System.out.println();
                System.out.println("No SSTables found.");
            }else{
                for(SSTableInfo table:tables){
                    System.out.println();
                    System.out.println("============================================================");
                    System.out.println(table.getFileName());
                    System.out.println("============================================================");
                    System.out.printf("%-11s : %d%n","Entries",table.getEntryCount());
                    System.out.printf("%-11s : %s%n","Size",formatSize(table.getSizeBytes()));
                    System.out.printf("%-11s : %s%n","Created At",formatTimestamp(table.getCreatedTime()));
                }
            }
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to load SSTables.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private String formatSize(long bytes){
        if(bytes<1024){
            return bytes+" Bytes";
        }
        double value=bytes;
        String[] units={"KB","MB","GB","TB"};
        int unit=0;
        while(value>=1024&&unit<units.length){
            value/=1024;
            unit++;
        }
        return String.format("%.2f %s",value,units[unit-1]);
    }
    private void walExplorer(){
        try{
            WALInfo info=database.getWALInfo();
            List<WALRecordInfo> records = database.getWALRecords();
            System.out.println();
            System.out.println("Write-Ahead Log (WAL)");
            System.out.println("------------------------------------------------------------");
            System.out.println("Exists         : "+(info.exists()?"Yes":"No"));
            System.out.println("Size           : "+formatSize(info.getSizeBytes()));
            System.out.println("Total Records  : "+records.size());
            if(records.isEmpty()){
                System.out.println();
                System.out.println("WAL is empty.");
            }else{
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.printf("%-12s %-30s %s%n","Operation","Key","Value");
                System.out.println("------------------------------------------------------------");
                for(WALRecordInfo record:records){
                    String key = new String(record.getKey(),StandardCharsets.UTF_8);
                    String value="-";
                    if(record.getValue()!=null){
                        value=new String(record.getValue(),StandardCharsets.UTF_8);
                    }
                    System.out.printf("%-12s %-30s %s%n",record.getOperation(),key,value);
                }
            }
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to load WAL.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void runtimeMonitor(){
        if(!requireDatabase("Runtime Monitor")){
            return;
        }
        try{
            DatabaseSummary summary=database.getSummary();
            RuntimeStatistics statistics=summary.getRuntimeStatistics();
            Runtime runtime=Runtime.getRuntime();
            long runtimeMillis=System.currentTimeMillis()-database.getOpenedAt();
            long heapMax=runtime.maxMemory();
            long heapTotal=runtime.totalMemory();
            long heapFree=runtime.freeMemory();
            long heapUsed=heapTotal-heapFree;
            double usage=0;
            if(summary.getConfiguration().getMemTableMaxSizeBytes()>0){
                usage=(summary.getMemTableInfo().getEstimatedSizeBytes()*100.0)/
                        summary.getConfiguration().getMemTableMaxSizeBytes();
            }
            long bytesRemaining=Math.max(0,summary.getConfiguration().getMemTableMaxSizeBytes()-summary.getMemTableInfo().getEstimatedSizeBytes());
            Long lastWalWrite=getLastEventTimestamp("PUT","DELETE");
            Long lastFlush=getLastEventTimestamp("FLUSH");
            Long lastSSTable=getLastSSTableCreationTime();
            System.out.println();
            System.out.println("Runtime Monitor");
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("SESSION INFORMATION");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Database Name",summary.getDatabaseInfo().getDatabaseName());
            System.out.printf("%-24s : %s%n","Database UUID",summary.getDatabaseInfo().getDatabaseUuid());
            System.out.printf("%-24s : %s%n","Database Status",database.isOpen()?"OPEN":"CLOSED");
            System.out.printf("%-24s : %s%n","Database Path",database.getDatabasePath());
            System.out.printf("%-24s : %s%n","Opened At",formatTimestamp(database.getOpenedAt()));
            System.out.printf("%-24s : %s%n","Runtime",formatDuration(runtimeMillis));
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("ENGINE STATISTICS");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %,d%n","Total Reads",summary.getRuntimeStatistics().getReadCount());
            System.out.printf("%-24s : %,d%n","Total Writes",summary.getRuntimeStatistics().getWriteCount());
            System.out.printf("%-24s : %,d%n","Total Deletes",summary.getRuntimeStatistics().getDeleteCount());
            System.out.printf("%-24s : %,d%n","Total Flushes",summary.getRuntimeStatistics().getFlushCount());
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("STORAGE ACTIVITY");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Last WAL Write",formatTimestamp(lastWalWrite));
            System.out.printf("%-24s : %s%n","Last Flush",formatTimestamp(lastFlush));
            System.out.printf("%-24s : %s%n","Last SSTable Created",formatTimestamp(lastSSTable));
            System.out.printf("%-24s : %,d%n","SSTable Count",summary.getStorageStatistics().getSstableCount());
            System.out.printf("%-24s : %s%n","Total SSTable Size",formatSize(summary.getStorageStatistics().getTotalSSTableSizeBytes()));
            System.out.printf("%-24s : %s%n","Current WAL Size",formatSize(summary.getWalInfo().getSizeBytes()));
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("MEMORY UTILIZATION");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %,d%n","MemTable Entries",summary.getMemTableInfo().getEntryCount());
            System.out.printf("%-24s : %s%n","Current Size",formatSize(summary.getMemTableInfo().getEstimatedSizeBytes()));
            System.out.printf("%-24s : %s%n","Maximum Size",formatSize(summary.getConfiguration().getMemTableMaxSizeBytes()));
            System.out.printf("%-24s : %.2f%%%n","Usage",usage);
            System.out.printf("%-24s : %s%n","Bytes Remaining",formatSize(bytesRemaining));
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("PERFORMANCE COUNTERS");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Average Read Time",formatDuration(statistics.getAverageReadTimeNanos()));
            System.out.printf("%-24s : %s%n","Average Write Time",formatDuration(statistics.getAverageWriteTimeNanos()));
            System.out.printf("%-24s : %s%n","Fastest Read",formatDuration(statistics.getFastestReadTimeNanos()));
            System.out.printf("%-24s : %s%n","Slowest Read",formatDuration(statistics.getSlowestReadTimeNanos()));
            System.out.printf("%-24s : %s%n","Fastest Write",formatDuration(statistics.getFastestWriteTimeNanos()));
            System.out.printf("%-24s : %s%n","Slowest Write",formatDuration(statistics.getSlowestWriteTimeNanos()));
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("DATABASE PROPERTIES");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Storage Engine","LSM Tree");
            System.out.printf("%-24s : %s%n","Storage Format","JDBS");
            System.out.printf("%-24s : %s%n","WAL Format","WAL");
            System.out.printf("%-24s : %s%n","Compression","None");
            System.out.printf("%-24s : %s%n","Checksum","CRC32");
            System.out.printf("%-24s : %s%n","Encoding","UTF-8");
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("JAVA / JVM INFORMATION");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Java Version",System.getProperty("java.version"));
            System.out.printf("%-24s : %s%n","JVM Vendor",System.getProperty("java.vendor"));
            System.out.printf("%-24s : %s%n","Operating System",System.getProperty("os.name"));
            System.out.printf("%-24s : %s%n","Architecture",System.getProperty("os.arch"));
            System.out.printf("%-24s : %,d%n","Available Processors",runtime.availableProcessors());
            System.out.printf("%-24s : %s%n","Heap Used",formatSize(heapUsed));
            System.out.printf("%-24s : %s%n","Heap Free",formatSize(heapFree));
            System.out.printf("%-24s : %s%n","Heap Max",formatSize(heapMax));
            System.out.println();
            System.out.println("================================================================================");
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to load Runtime Monitor.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private Long getLastEventTimestamp(String... eventTypes){
        List<EngineEvent> events=database.getEventLog().getEvents();
        for(int i=events.size()-1;i>=0;i--){
            EngineEvent event=events.get(i);
            for(String type:eventTypes){
                if(type.equals(event.getType())){
                    return event.getTimestamp();
                }
            }
        }
        return null;
    }
    private Long getLastSSTableCreationTime(){
        try{
            long last=0;
            for(SSTableInfo table:database.getSSTableInfo()){
                if(table.getCreatedTime()>last){
                    last=table.getCreatedTime();
                }
            }
            return last==0?null:last;
        }catch(IOException e){
            return null;
        }
    }
    private String formatTimestamp(Long timestamp){
        if(timestamp==null){
            return "Never";
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    private void healthMonitor(){
        if(!requireDatabase("Health Monitor")){
            return;
        }
        try{
            DatabaseSummary summary=database.getSummary();
            System.out.println();
            System.out.println("Health Monitor");
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("OVERALL HEALTH");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Status",summary.getHealthReport().getStatus());
            System.out.printf("%-24s : %s%n","Message",summary.getHealthReport().getMessage());
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("DATABASE");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Database Name",summary.getDatabaseInfo().getDatabaseName());
            System.out.printf("%-24s : %s%n","Database Status",database.isOpen()?"OPEN":"CLOSED");
            System.out.printf("%-24s : %s%n","Database Path",database.getDatabasePath());
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("STORAGE");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Database Size",formatSize(database.getDatabaseSize()));
            System.out.printf("%-24s : %,d%n","SSTable Count",summary.getStorageStatistics().getSstableCount());
            System.out.printf("%-24s : %,d%n","MemTable Entries",summary.getMemTableInfo().getEntryCount());
            System.out.printf("%-24s : %s%n","MemTable Size",formatSize(summary.getMemTableInfo().getEstimatedSizeBytes()));
            System.out.printf("%-24s : %s%n","WAL Exists",summary.getWalInfo().exists()?"Yes":"No");
            System.out.printf("%-24s : %s%n","WAL Size",formatSize(summary.getWalInfo().getSizeBytes()));
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("CONFIGURATION");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-24s : %s%n","Auto Flush",summary.getConfiguration().isAutoFlushEnabled()?"Enabled":"Disabled");
            System.out.printf("%-24s : %s%n","MemTable Limit",formatSize(summary.getConfiguration().getMemTableMaxSizeBytes()));
            System.out.println();
            System.out.println("================================================================================");
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to load Health Monitor.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void eventLog(){
        if(!requireDatabase("Event Log")){
            return;
        }
        while(true){
            System.out.println();
            System.out.println("Event Log");
            System.out.println("================================================================================");
            List<EngineEvent> events=database.getEventLog().getEvents();
            System.out.println();
            System.out.println("Total Events : "+events.size());
            if(events.isEmpty()){
                System.out.println();
                System.out.println("No events recorded.");
            }else{
                System.out.println();
                System.out.println("--------------------------------------------------------------------------------");
                System.out.printf("%-20s %-15s %s%n","Timestamp","Type","Message");
                System.out.println("--------------------------------------------------------------------------------");
                for(EngineEvent event:events){
                    System.out.printf("%-20s %-15s %s%n",
                            formatTimestamp(event.getTimestamp()),
                            event.getType(),
                            event.getMessage());
                }
            }
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("1. Clear Event Log");
            System.out.println("2. Back");
            System.out.println();
            System.out.print("Select Option: ");
            String option=scanner.nextLine().trim();
            switch(option){
                case "1":
                    database.clearEventLog();
                    System.out.println();
                    System.out.println("Event log cleared successfully.");
                    pause();
                    break;
                case "2":
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }
    private void settings(){
        if(!requireDatabase("Settings")){
            return;
        }
        while(true){
            try{
                DatabaseSummary summary=database.getSummary();
                System.out.println();
                System.out.println("Settings");
                System.out.println("================================================================================");
                System.out.println();
                System.out.println("Current Configuration");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.printf("%-24s : %s%n","Auto Flush",
                        summary.getConfiguration().isAutoFlushEnabled()?"Enabled":"Disabled");
                System.out.printf("%-24s : %s%n","MemTable Limit",
                        formatSize(summary.getConfiguration().getMemTableMaxSizeBytes()));
                System.out.println();
                System.out.println("================================================================================");
                System.out.println();
                System.out.println("1. Toggle Auto Flush");
                System.out.println("2. Change MemTable Limit");
                System.out.println("3. Back");
                System.out.println();
                System.out.print("Select Option: ");
                String option=scanner.nextLine().trim();
                switch(option){
                    case "1":
                        database.setAutoFlushEnabled(
                                !summary.getConfiguration().isAutoFlushEnabled());
                        System.out.println();
                        System.out.println("Auto Flush updated successfully.");
                        pause();
                        break;
                    case "2":
                        changeMemTableLimit();
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println();
                        System.out.println("Invalid option.");
                        pause();
                }
            }catch(Exception e){
                System.out.println();
                System.out.println("Failed to load settings.");
                System.out.println(e.getMessage());
                pause();
                return;
            }
        }
    }
    private boolean confirmMemTableFlush(long currentSize,long newLimit){
        System.out.println();
        System.out.println("Current MemTable Usage : "+formatSize(currentSize));
        System.out.println("Requested Limit        : "+formatSize(newLimit));
        System.out.println();
        System.out.println("The current MemTable exceeds the requested limit.");
        System.out.println("Changing the limit will flush the MemTable.");
        System.out.println();
        System.out.print("Proceed (Y/N)? ");
        String answer=scanner.nextLine().trim();
        return answer.equalsIgnoreCase("Y");
    }
    private void changeMemTableLimit(){
        try{
            System.out.println();
            System.out.print("New MemTable Limit (MB): ");
            long mb=Long.parseLong(scanner.nextLine().trim());
            if(mb<=0){
                System.out.println();
                System.out.println("Limit must be greater than zero.");
                pause();
                return;
            }
            long newLimit=mb*1024L*1024L;
            long currentSize=database.getMemTableInfo().getEstimatedSizeBytes();
            if(currentSize>newLimit){
                if(!confirmMemTableFlush(currentSize,newLimit)){
                    System.out.println();
                    System.out.println("Operation cancelled.");
                    pause();
                    return;
                }
                database.flush();
                System.out.println();
                System.out.println("MemTable flushed successfully.");
            }
            database.setMemTableMaxSizeBytes(newLimit);
            System.out.println();
            System.out.println("MemTable limit updated successfully.");
        }catch(NumberFormatException e){
            System.out.println();
            System.out.println("Please enter a valid number.");
        }catch(Exception e){
            System.out.println();
            System.out.println("Failed to update MemTable limit.");
            System.out.println(e.getMessage());
        }
        pause();
    }
    private void about(){
        System.out.println();
        System.out.println("About");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("JaveDB");
        System.out.println();
        System.out.println("An Observable Embedded LSM Database written in Java.");
        System.out.println();
        System.out.println("JaveDB implements the core components of an LSM-tree database—including");
        System.out.println("MemTables, Write-Ahead Logging, SSTables, Compaction, and Bloom Filters—");
        System.out.println("while exposing them through an interactive observability layer that allows");
        System.out.println("users to inspect, monitor, and understand the database internals in real time.");
        System.out.println();
        System.out.println("© mo5ul. All rights reserved.");
        System.out.println();
        System.out.println("Thank you for using JaveDB.");
        System.out.println();
        System.out.println("================================================================================");
        pause();
    }
    private boolean requireDatabase(String section){
        System.out.println();
        System.out.println(section);
        System.out.println();
        if(database==null||!database.isOpen()){
            System.out.println("No database is currently open!");
            System.out.println("Please go back and select 'Create/Open Database' first!");
            pause();
            return false;
        }
        return true;
    }
    private void closeDatabase(){
        if(database!=null&&database.isOpen()){
            try{
                database.close();
            }catch(IOException ignored){
            }
        }
    }
    private void pause(){
        System.out.println();
        System.out.print("Press *Enter* to continue...");
        scanner.nextLine();
    }
    private String formatDuration(long nanos){
        if(nanos==0){
            return "N/A";
        }

        if(nanos<1000){
            return nanos+" ns";
        }

        if(nanos<1_000_000){
            return String.format("%.2f µs",nanos/1_000.0);
        }

        return String.format("%.2f ms",nanos/1_000_000.0);
    }
}