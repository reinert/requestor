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

import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.gdeferred.impl.DeferredObject;

class DeferredSingleResult<T> extends DeferredObject<T, Throwable, RequestProgress> implements DeferredRequest<T> {

    private static Logger logger = Logger.getLogger(DeferredSingleResult.class.getName());

    private final Class<T> responseType;
    private final SerializationEngine serializationEngine;

    public DeferredSingleResult(Class<T> responseType, SerializationEngine serializationEngine) {
        this.responseType = responseType;
        this.serializationEngine = serializationEngine;
    }

    @Override
    public DeferredRequest<T> resolve(Request request, Response response) {
        // Check if access to Response was requested
        if (responseType == io.reinert.requestor.Response.class) {
            @SuppressWarnings("unchecked")
            final T result = (T) response;
            super.resolve(result);
            return this;
        }

        final Headers headers = response.getHeaders();
        String responseContentType = headers.getValue("Content-Type");
        if (responseContentType == null) {
            responseContentType = "*/*";
            logger.log(Level.INFO, "Response with no 'Content-Type' header received from '" + request.getUrl()
                    + "'. The content-type value has been automatically set to '*/*' to match deserializers.");
        }

        T result = serializationEngine.deserialize(response.getText(), responseType, responseContentType,
                request.getUrl(), headers);

        super.resolve(result);
        return this;
    }
}
