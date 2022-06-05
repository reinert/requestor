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
package io.reinert.requestor.core;

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * Read progress of requests.
 *
 * @author Danilo Reinert
 */
public class ReadProgress extends WriteProgress {

    private final ResponseHeader response;

    public ReadProgress(SerializedRequest request, ResponseHeader response, ProgressEvent progressEvent) {
        this(request, response, progressEvent, null);
    }

    public ReadProgress(SerializedRequest request, ResponseHeader response, ProgressEvent progressEvent,
                        SerializedPayload chunk) {
        super(request, progressEvent, chunk);
        this.response = response;
    }

    public ResponseHeader getResponse() {
        return response;
    }
}
