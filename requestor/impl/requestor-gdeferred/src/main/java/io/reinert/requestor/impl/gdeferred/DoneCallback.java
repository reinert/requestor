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
package io.reinert.requestor.impl.gdeferred;

import io.reinert.requestor.RawResponseImpl;
import io.reinert.requestor.Response;

/**
 * DoneCallback with optional access to the response.
 *
 * @param <T> Type of the response payload
 */
public abstract class DoneCallback<T> implements io.reinert.gdeferred.DoneCallback<T> {

    @Override
    public void onDone(T result) {
    }

    /**
     * If you want to access the Response attributes, then override this method.
     *
     * @param response  the HTTP Response returned from the Request.
     */
    @SuppressWarnings("unchecked")
    public void onDone(Response<T> response) {
        if (response instanceof RawResponseImpl) {
            onDone((T) response);
            return;
        }

        onDone(response.getPayload());
    }
}
