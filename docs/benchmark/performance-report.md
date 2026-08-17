# JaveDB Performance Report

> Automatically generated benchmark report for JaveDB.

**Database:** JaveDB-Demo
**Generated:** 2026-08-17 10:37:09
**Database Path:** /home/mo5ul/Jave/DB/JaveDB-Demo

---

## Storage Engine

| Property | Value |
|---|---|
| SSTables | 1 |
| Bloom Filter | Enabled |
| Sparse Index | Every 16 records |
| Automatic Compaction | Enabled |

---

## Benchmark Results

| Test | Operations | Throughput (ops/sec) | Avg Latency |
|---|---:|---:|---:|
| PUT Benchmark | 10,000 | 227,630.99 | 4.39 µs |
| GET Benchmark | 10,000 | 2,604,487.48 | 0.38 µs |
| Stress Test | 25,000 | 361,237.35 | 2.77 µs |
| Large Dataset Test | 50,000 | 238,936.42 | 4.19 µs |
| Reliability Test | 5,000 | 95.71 | 10448.30 µs |

---

## Runtime Statistics

| Metric | Value |
|---|---:|
| Reads | 22568 |
| Writes | 72432 |
| Deletes | 1682 |
| Flushes | 1 |
| Compactions | 1 |

---

## Storage Statistics

| Metric | Value |
|---|---:|
| MemTable Entries | 0 |
| MemTable Size | 0 bytes |
| SSTable Files | 1 |
| Total SSTable Size | 2700397 bytes |

---

## Assessment

- **Bloom Filter:** PASS
- **Sparse Index:** PASS
- **Automatic Compaction:** PASS
- **Recovery Verification:** PASS
