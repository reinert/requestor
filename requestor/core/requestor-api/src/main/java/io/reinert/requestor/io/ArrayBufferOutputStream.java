package io.reinert.requestor.io;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gwt.typedarrays.client.ArrayBufferNative;
import com.google.gwt.typedarrays.client.Int8ArrayNative;

public class ArrayBufferOutputStream extends OutputStream {

    private final JsArrayByte array = JsArrayByte.createArray().cast();

    public ArrayBufferOutputStream() {
    }

    public void write(int b) throws IOException {
        array.push((byte) b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0 ; i < len ; i++) {
            write(b[off + i]);
        }
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }

    public ArrayBufferNative toArrayBuffer() {
        ArrayBufferNative buffer = ArrayBufferNative.create(array.length());
        Int8ArrayNative byteBuffer = Int8ArrayNative.create(buffer);
        byteBuffer.set(array.toByteArray());
        return buffer;
    }
}
