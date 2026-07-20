package io.github.mohul.wal;

import java.nio.file.StandardOpenOption;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public final class WALManager {
    private static final byte PUT = 1;
    private static final byte DELETE = 2;
    private final Path walPath;
    private DataOutputStream output;
    public WALManager(Path walPath) throws IOException {
        if (walPath == null) {
            throw new IllegalArgumentException("WAL path cannot be null.");
        }
        this.walPath = walPath;
        openOutput(); 
    }
    public void append(WALRecord record) throws IOException {
        if (record == null) {
            throw new IllegalArgumentException("WAL record cannot be null.");
        }
        byte[] key = record.getKey();
        byte[] value = record.getValue();
        if (record.getOperation() == OperationType.PUT) {
            output.writeByte(PUT);
        } else {
            output.writeByte(DELETE);
        }
        output.writeInt(key.length);
        output.write(key);
        if (value == null) {
            output.writeInt(0);
        } else {
            output.writeInt(value.length);
            output.write(value);
        }
        output.flush();
    }
    public List<WALRecord> replay() throws IOException {
        List<WALRecord> records = new ArrayList<>();
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(
                        Files.newInputStream(walPath)))) {
            while (true) {
                try {
                    byte operation = input.readByte();
                    int keyLength = input.readInt();
                    byte[] key = new byte[keyLength];
                    input.readFully(key);
                    int valueLength = input.readInt();
                    byte[] value = null;
                    if (valueLength > 0) {
                        value = new byte[valueLength];
                        input.readFully(value);
                    }
                    OperationType operationType;
                    if (operation == PUT) {
                        operationType = OperationType.PUT;
                    } else if (operation == DELETE) {
                        operationType = OperationType.DELETE;
                    } else {
                        throw new IOException("Invalid WAL operation: " + operation);
                    }
                    records.add(new WALRecord(operationType, key, value));
                } catch (EOFException e) {
                    break;
                }
            }
        }
        return records;
    }
    public void close() throws IOException {
        output.close();
    }
    private void openOutput() throws IOException {
    output = new DataOutputStream(
            new BufferedOutputStream(
                    Files.newOutputStream(
                            walPath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                    )
                )
        );
    }
    public void reset() throws IOException {
        output.close();

        Files.deleteIfExists(walPath);

        openOutput();
    }
}