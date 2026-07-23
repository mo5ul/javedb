package io.github.mohul.observability.info;

import io.github.mohul.observability.health.HealthReport;
import io.github.mohul.observability.statistics.RuntimeStatistics;
import io.github.mohul.observability.statistics.StorageStatistics;
import io.github.mohul.observability.storage.MemTableInfo;
import io.github.mohul.observability.storage.WALInfo;
public final class DatabaseSummary {
    private final DatabaseInfo databaseInfo;
    private final RuntimeStatistics runtimeStatistics;
    private final StorageStatistics storageStatistics;
    private final MemTableInfo memTableInfo;
    private final WALInfo walInfo;
    private final DatabaseConfiguration configuration;
    private final HealthReport healthReport;
    private final boolean open;
    private final long openedAt;
    public DatabaseSummary(DatabaseInfo databaseInfo,RuntimeStatistics runtimeStatistics,StorageStatistics storageStatistics,MemTableInfo memTableInfo,WALInfo walInfo,DatabaseConfiguration configuration,HealthReport healthReport,boolean open,long openedAt){
        this.databaseInfo=databaseInfo;
        this.runtimeStatistics=runtimeStatistics;
        this.storageStatistics=storageStatistics;
        this.memTableInfo=memTableInfo;
        this.walInfo=walInfo;
        this.configuration=configuration;
        this.healthReport=healthReport;
        this.open=open;
        this.openedAt=openedAt;
    }
    public DatabaseInfo getDatabaseInfo(){
        return databaseInfo;
    }
    public RuntimeStatistics getRuntimeStatistics(){
        return runtimeStatistics;
    }
    public StorageStatistics getStorageStatistics(){
        return storageStatistics;
    }
    public MemTableInfo getMemTableInfo(){
        return memTableInfo;
    }
    public WALInfo getWalInfo(){
        return walInfo;
    }
    public DatabaseConfiguration getConfiguration(){
        return configuration;
    }
    public HealthReport getHealthReport(){
        return healthReport;
    }
    public boolean isOpen(){
        return open;
    }
    public long getOpenedAt(){
        return openedAt;
    }
}