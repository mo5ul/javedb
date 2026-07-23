package io.github.mohul.metadata;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import io.github.mohul.observability.info.DatabaseInfo;
public class MetadataReader implements Closeable {
    private final DataInputStream input;
    public MetadataReader(Path metadataPath) throws IOException {
        input = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(metadataPath.toFile())));
    }
    public DatabaseInfo read() throws IOException {
        int magicNumber = input.readInt();
        if (magicNumber != MetadataConstants.MAGIC_NUMBER) {
            throw new IOException("Invalid metadata file.");
        }
        String databaseUuid = input.readUTF();
        String databaseName = input.readUTF();
        long createdAt = input.readLong();
        return new DatabaseInfo(
                databaseName,
                databaseUuid,
                createdAt);
    }
    @Override
    public void close() throws IOException {
        input.close();
    }

}