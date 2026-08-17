# JaveDB Public API

This document describes the primary public API exposed by JaveDB.

---

# Initialization

## JaveDBOptions

Configures database behavior before opening a database.

```java
JaveDBOptions options = new JaveDBOptions();
options.setAutoFlushEnabled(true);
```

### Available Configuration

| Method | Description |
|---------|-------------|
| `setAutoFlushEnabled(boolean)` | Enables automatic MemTable flushing |

---

# Opening a Database

```java
Path dbPath = Path.of("JaveDB-Demo");

JaveDB database = new JaveDB(dbPath, options);
```

The database directory is created automatically if it does not exist.

---

# Write Operations

## put()

Stores or updates a key-value pair.

```java
database.put(
    "username".getBytes(),
    "mohul".getBytes()
);
```

**Behavior**

- Appends to the Write-Ahead Log
- Updates the MemTable
- Overwrites existing values

---

# Read Operations

## get()

Retrieves a value using its key.

```java
byte[] value = database.get("username".getBytes());
```

Returns `null` if the key does not exist.

---

# Delete Operations

## delete()

Deletes a key using a tombstone record.

```java
database.delete("username".getBytes());
```

Deletion becomes permanent after SSTable compaction.

---

# Utility Operations

## listKeys()

Returns all currently available keys.

```java
List<String> keys = database.listKeys();
```

Used by the interactive CLI to display available keys during GET and PUT operations.

---

# Runtime Information

## getSummary()

Returns a complete database summary.

```java
DatabaseSummary summary = database.getSummary();
```

Includes:

- Database metadata
- Storage statistics
- MemTable information
- Runtime statistics

---

# Performance Suite

The benchmark framework is available through the CLI and internally consists of:

| Class | Purpose |
|--------|---------|
| `BenchmarkRunner` | PUT & GET benchmarks |
| `StressTester` | High-volume write testing |
| `LargeDatasetTester` | Large dataset evaluation |
| `ReliabilityTester` | Recovery verification |
| `ReportGenerator` | Markdown benchmark reports |

---

# Storage Engine Components

| Component | Responsibility |
|-----------|----------------|
| WAL | Durable write logging |
| MemTable | In-memory sorted writes |
| SSTable | Immutable persistent storage |
| Bloom Filter | Fast negative lookups |
| Sparse Index | Efficient binary search |
| Compaction | Merge and cleanup SSTables |