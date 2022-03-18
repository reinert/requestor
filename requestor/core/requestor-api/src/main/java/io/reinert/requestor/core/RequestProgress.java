/*
 * Copyright 2014-2022 Danilo Reinert
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
 * Progress data of requests.
 *
 * @author Danilo Reinert
 */
public class RequestProgress {

    private final ProgressEvent requestProgress;
    private final SerializedPayload serializedPayload;

    public RequestProgress(ProgressEvent requestProgress) {
        this(requestProgress, null);
    }

    public RequestProgress(ProgressEvent requestProgress, SerializedPayload serializedPayload) {
        this.requestProgress = requestProgress;
        this.serializedPayload = serializedPayload;
    }

    public boolean isLengthComputable() {
        return requestProgress.lengthComputable();
    }

    public boolean isChunkAvailable() {
        return serializedPayload != null;
    }

    public SerializedPayload getChunk() {
        return serializedPayload;
    }

    /**
     * Returns the loaded amount of the request.
     * <p></p>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The loaded amount if available, 0 otherwise
     */
    public int getLoaded() {
        return requestProgress.loaded();
    }

    /**
     * Returns the total amount of the request.
     * <p></p>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The total amount if available, 0 otherwise
     */
    public int getTotal() {
        return requestProgress.total();
    }

    /**
     * Returns the completed amount of the request in a interval [0,1].
     * This value is calculated by dividing the loaded amount by the total.
     * <p></p>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The completed amount if available, 0 otherwise
     */
    public double getCompletedFraction() {
        return getCompletedFraction(1);
    }

    /**
     * Returns the completed amount of the request in a interval [0,1] multiplied by the given factor.
     * This value is calculated by dividing the loaded amount by the total and then multiplying by the factor.
     * <p></p>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The completed amount if available, 0 otherwise
     */
    public double getCompletedFraction(int multiplyingFactor) {
        if (!isLengthComputable()) return 0.0;
        return ((double) getLoaded() / (double) getTotal()) * (double) multiplyingFactor;
    }
}
