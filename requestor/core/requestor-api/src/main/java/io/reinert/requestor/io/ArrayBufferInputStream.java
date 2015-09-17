package io.reinert.requestor.io;

import java.io.IOException;
import java.io.InputStream;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;

public class ArrayBufferInputStream extends InputStream {

    private final Int8Array array;
    private int cursor = 0;

    public ArrayBufferInputStream(ArrayBuffer buffer) {
        this.array = Int8ArrayNative.create(buffer);
    }

    @Override
    public int read() throws IOException {
        if (available() == 0) {
            return -1;
        }
        advanceCursor();
        return array.get(cursor - 1);
    }

    @Override
    public int available() throws IOException {
        return array.length() - cursor;
    }

    private void advanceCursor() {
        cursor++;
    }
}
