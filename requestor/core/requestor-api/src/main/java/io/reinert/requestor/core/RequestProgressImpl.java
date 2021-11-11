/*
 * Copyright 2014 Danilo Reinert
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

/**
 * Default implementation of {@link RequestProgress}.
 *
 * @author Danilo Reinert
 */
public class RequestProgressImpl implements RequestProgress {

    private final ProgressEvent requestProgress;

    public RequestProgressImpl(ProgressEvent requestProgress) {
        this.requestProgress = requestProgress;
    }

    @Override
    public boolean isLengthComputable() {
        return requestProgress.lengthComputable();
    }

    @Override
    public double getLoaded() {
        return requestProgress.loaded();
    }

    @Override
    public double getTotal() {
        return requestProgress.total();
    }

    @Override
    public double getCompletedFraction() {
        return getCompletedFraction(1);
    }

    @Override
    public double getCompletedFraction(double multiplyingFactor) {
        if (!isLengthComputable())
            return 0;
        return (getLoaded() / getTotal()) * multiplyingFactor;
    }
}
