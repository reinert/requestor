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
package io.reinert.requestor;

/**
 * Deferred for requests expecting a single object.
 *
 * @param <T>   Expected type in {@link RequestPromise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public class DeferredSingleResult<T> extends DeferredRequest<T> {

    private final Class<T> responseType;

    public DeferredSingleResult(ResponseProcessor processor, Class<T> responseType) {
        super(processor);
        this.responseType = responseType;
    }

    @Override
    protected DeserializedResponse<T> process(ResponseProcessor processor, Request request,
                                              SerializedResponseContext response) {
        return processor.process(request, response, responseType);
    }
}
