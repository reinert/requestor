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

import java.util.Collection;

/**
 * Deferred for requests expecting an array of objects.
 *
 * @param <T>   Expected type in {@link RequestPromise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public class DeferredCollectionResult<T> extends DeferredRequest<Collection<T>> {

    private final Class<T> responseType;
    private final Class<? extends Collection> containerType;

    public DeferredCollectionResult(ResponseProcessor processor, Class<T> responseType,
                                    Class<? extends Collection> containerType) {
        super(processor);
        this.responseType = responseType;
        this.containerType = containerType;
    }

    @Override
    protected DeserializedResponse<Collection<T>> process(ResponseProcessor processor, Request request,
                                                          SerializedResponse response) {
        return processor.process(request, response, responseType, containerType);
    }
}
