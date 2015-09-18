/*
 * Copyright 2015 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.io;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gwt.typedarrays.client.ArrayBufferNative;
import com.google.gwt.typedarrays.client.Int8ArrayNative;

public class ArrayBufferOutputStream extends OutputStream {

    private final JsArrayByte array;

    public ArrayBufferOutputStream() {
        array = JsArrayByte.createArray().cast();
    }

    private ArrayBufferOutputStream(JsArrayByte array) {
        this.array = array;
    }

    @Override
    public void write(int b) throws IOException {
        array.push((byte) b);
    }

//    public void write(byte b[]) throws IOException {
//        write(b, 0, b.length);
//    }
//
//    public void write(byte b[], int off, int len) throws IOException {
//        if (b == null) {
//            throw new NullPointerException();
//        } else if ((off < 0) || (off > b.length) || (len < 0) ||
//                ((off + len) > b.length) || ((off + len) < 0)) {
//            throw new IndexOutOfBoundsException();
//        } else if (len == 0) {
//            return;
//        }
//        for (int i = 0 ; i < len ; i++) {
//            write(b[off + i]);
//        }
//    }
//
//    public void flush() throws IOException {
//    }
//
//    public void close() throws IOException {
//    }

    public ArrayBufferNative toArrayBuffer() {
        ArrayBufferNative buffer = ArrayBufferNative.create(array.length());
        Int8ArrayNative byteBuffer = Int8ArrayNative.create(buffer);
        byteBuffer.set(array.toByteArray());
        return buffer;
    }
}
