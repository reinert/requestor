/*
 * Copyright 2021 Danilo Reinert
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
package io.reinert.requestor.gwt.xhr;

import io.reinert.requestor.core.ProgressEvent;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestOptions;

/**
 * Thrown to indicate that an HTTP request has timed out.
 *
 * @author Danilo Reinert
 */
public class NetworkErrorException extends RequestCancelException {

    private static final long serialVersionUID = -1933871064668352744L;

    private ProgressEvent progress;

    protected NetworkErrorException() {
        super();
    }

    /**
     * Constructs a network error exception for the given {@link RequestOptions}.
     *
     * @param requestOptions    the request which timed out
     * @param progress          the progress of the request
     */
    public NetworkErrorException(RequestOptions requestOptions, ProgressEvent progress) {
        super(requestOptions, "Request was interrupted due to network error.");
        this.progress = progress;
    }

    /**
     * Returns the request progress.
     *
     * @return the request progress
     */
    public ProgressEvent getProgress() {
        return progress;
    }
}
