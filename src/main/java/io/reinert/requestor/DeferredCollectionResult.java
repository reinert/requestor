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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Response;

import io.reinert.gdeferred.impl.DeferredObject;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.HttpDeserializationContext;
import io.reinert.requestor.serialization.SerdesManager;

class DeferredCollectionResult<T> extends DeferredObject<Collection<T>, Throwable, RequestProgress>
        implements DeferredRequest<Collection<T>> {

    private static Logger logger = Logger.getLogger(DeferredCollectionResult.class.getName());

    private final Class<T> responseType;
    private final Class<? extends Collection> containerType;
    private final SerdesManager serdesManager;
    private final ProviderManager providerManager;

    public DeferredCollectionResult(Class<T> responseType, Class<? extends Collection> containerType,
                                    SerdesManager serdesManager, ProviderManager providerManager) {
        this.responseType = responseType;
        this.containerType = containerType;
        this.serdesManager = serdesManager;
        this.providerManager = providerManager;
    }

    @Override
    public DeferredRequest<Collection<T>> resolve(Response response) {
        final Headers headers = new Headers(response.getHeaders());
        String responseContentType = headers.getValue("Content-Type");
        if (responseContentType == null) {
            responseContentType = "*/*";
            logger.log(Level.WARNING, "Response with no 'Content-Type' header received." +
                    " The content-type value has been automatically set to '*/*' for matching deserializers.");
        }

        final Deserializer<T> deserializer = serdesManager.getDeserializer(responseType, responseContentType);
        final DeserializationContext context = new HttpDeserializationContext(headers, providerManager, responseType);
        @SuppressWarnings("unchecked")
        Collection<T> result = deserializer.deserializeAsCollection(containerType, response.getText(), context);

        super.resolve(result);
        return this;
    }

    @Override
    public DeferredRequest<Collection<T>> reject(Response response) {
        super.reject(new UnsuccessfulResponseException(new ResponseImpl(response)));
        return this;
    }
}
