package io.github.mohul.metadata;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import io.github.mohul.observability.info.DatabaseInfo;
public class MetadataWriter implements Closeable {
    private final DataOutputStream output;
    public MetadataWriter(Path metadataPath) throws IOException {
        output = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(metadataPath.toFile())));
    }
    public void write(DatabaseInfo databaseInfo) throws IOException {
        output.writeInt(MetadataConstants.MAGIC_NUMBER);
        output.writeUTF(databaseInfo.getDatabaseUuid());
        output.writeUTF(databaseInfo.getDatabaseName());
        output.writeLong(databaseInfo.getCreatedAt());
        output.flush();
    }
    @Override
    public void close() throws IOException {
        output.close();
    }
}