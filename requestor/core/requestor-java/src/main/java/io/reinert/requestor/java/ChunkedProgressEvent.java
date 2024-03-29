/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.java;

import io.reinert.requestor.core.ProgressEvent;

public class ChunkedProgressEvent implements ProgressEvent {

    private static final long serialVersionUID = -6384493486672310466L;

    private final long bytesLoaded;

    public ChunkedProgressEvent(long bytesLoaded) {
        this.bytesLoaded = bytesLoaded;
    }

    @Override
    public boolean lengthComputable() {
        return false;
    }

    @Override
    public long loaded() {
        return bytesLoaded;
    }

    @Override
    public long total() {
        return 0L;
    }
}
