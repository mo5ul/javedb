package io.github.mohul.observability.info;
public class DatabaseInfo {
    private final String databaseName;
    private final String databaseUuid;
    private final long createdAt;
    public DatabaseInfo(
            String databaseName,
            String databaseUuid,
            long createdAt) {
        this.databaseName = databaseName;
        this.databaseUuid = databaseUuid;
        this.createdAt = createdAt;
    }
    public String getDatabaseName() {
        return databaseName;
    }
    public String getDatabaseUuid() {
        return databaseUuid;
    }
    public long getCreatedAt() {
        return createdAt;
    }
}