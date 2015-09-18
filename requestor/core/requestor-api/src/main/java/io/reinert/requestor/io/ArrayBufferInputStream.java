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
