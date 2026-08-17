# JaveDB Usage Guide

This guide explains how to install, launch, and use JaveDB through its interactive command-line interface.

---

# Requirements

- Java 21
- Maven 3.9+

Verify installation:

```bash
java -version
mvn -version
```

---

# Running JaveDB

Clone the repository and start the application:

```bash
git clone <repository-url>
cd javedb
mvn compile
mvn exec:java -Dexec.mainClass="io.github.mohul.db.Main"
```

The interactive console will appear.

---

# Creating a Database

1. Select **Database Manager**
2. Choose **Create/Open Database**
3. Enter a database name (example: `JaveDB-Demo`)

JaveDB creates the required storage structure automatically.

---

# CRUD Operations

## Put Entry

Stores a new key-value pair.

Example:

- Key: `username`
- Value: `mohul`

The console displays existing keys before inserting a new entry.

## Get Entry

Retrieves the value associated with a key.

Example:

- Key: `username`

Output:

```text
Value : mohul
```

## Delete Entry

Deletes a key using a tombstone record. Deleted entries are removed permanently during SSTable compaction.

---

# Runtime Dashboard

The Dashboard provides live database information including:

- Database metadata
- Health status
- MemTable statistics
- SSTable count
- WAL information
- Storage utilization

This is useful for monitoring database health during execution.

---

# Performance Suite

JaveDB includes an integrated benchmarking framework.

Navigate to:

```text
Main Menu
  -> Performance Suite
```

The suite automatically executes:

- PUT Benchmark
- GET Benchmark
- Stress Test
- Large Dataset Test
- Reliability Test

A Markdown report is generated automatically after completion.

Example output location:

```text
<database>/reports/benchmark_YYYY-MM-DD_HH-MM-SS.md
```

---

# Health Monitor

The Health Monitor validates:

- Write-Ahead Log status
- Storage availability
- SSTable integrity
- Runtime health

A healthy database reports:

```text
Health : HEALTHY
```

---

# Project Structure

```text
javedb/
├── src/
├── docs/
├── screenshots/
├── LICENSE
├── README.md
└── pom.xml
```

---

# Notes

- JaveDB is an embedded database and requires no external server.
- All data is stored locally inside the selected database directory.
- Benchmark reports are generated in Markdown format for easy sharing and version control.