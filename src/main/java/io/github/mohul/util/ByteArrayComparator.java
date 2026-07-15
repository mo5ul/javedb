package io.github.mohul.util;

import java.util.Arrays;
import java.util.Comparator;

public final class ByteArrayComparator implements Comparator<byte[]> {

    @Override
    public int compare(byte[] o1, byte[] o2) {
        return Arrays.compare(o1, o2);
    }
}