/*
 * Copyright 2015 Danilo Reinert
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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.deferred.Promise;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

/**
 * This interface is a configurable container responsible for building requests.
 * Usually, you will use it as a singleton.
 * <p/>
 *
 * It provides a convenience API for managing/creating HTTP Requests.
 * <p/>
 *
 * You can register {@link RequestFilter} or {@link ResponseFilter}, to manipulate all your requests.
 * <p/>
 *
 * You can register {@link RequestInterceptor} or {@link ResponseInterceptor} to intercept every request/response and
 * modify their payloads.
 * <p/>
 *
 * You can register {@link Serializer}, {@link Deserializer} or {@link Serdes} to provide serialization/deserialization
 * of objects according to media-types.
 *
 * @author Danilo Reinert
 */
public abstract class Requestor implements SerdesManager, FilterManager, InterceptorManager, ProviderManager {

    public static Requestor newInstance() {
        return GWT.create(Requestor.class);
    }

    //===================================================================
    // Requestor configuration
    //===================================================================

    public abstract void setDefaultMediaType(String contentType);

    public abstract String getDefaultMediaType();

    public abstract <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType);

    public abstract <T> Serializer<T> getSerializer(Class<T> type, String mediaType);

    public abstract <T> Provider<T> getProvider(Class<T> type);

    public abstract <T> T getInstance(Class<T> type);

    //===================================================================
    // Request factory methods
    //===================================================================

    /**
     * Start building a request with the given url.
     *
     * @param url   The url of the request.
     *
     * @return  The request builder.
     */
    public abstract RequestInvoker req(String url);

    /**
     * Build a new web resource target.
     *
     * @param uri  Stringified URI of the target resource. May contain URI template parameters.
     *             Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public abstract WebTarget target(String uri);

    /**
     * Build a new web resource target.
     *
     * @param uri  Web resource URI represented. Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public abstract WebTarget target(Uri uri);

    /**
     * Build a new web resource target.
     *
     * @param uriBuilder  Web resource URI represented as URI builder. Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public abstract WebTarget target(UriBuilder uriBuilder);

    /**
     * Build a new web resource target.
     *
     * @param link  Link to a web resource. Must not be {@code null}.
     *
     * @return  Web resource target bound to the link web resource.
     */
    public abstract WebTarget target(Link link);

    /**
     * Quickly dispatch serialized requests.
     * Use it if you want to skip the request processing steps and send a request as it is.
     *
     * @param request       The request to be sent
     * @param expectedType  The expected type class of the response
     * @param <T>           The expected type of the response
     *
     * @return  Promise of the expected type
     */
    public abstract <T> Promise<T> dispatch(SerializedRequest request, Class<T> expectedType);

    /**
     * Quickly dispatch serialized requests.
     * Use it if you want to skip the request processing steps and send a request as it is.
     *
     * @param request       The request to be sent
     * @param expectedType  The expected type class of the response
     * @param containerType The container to accumulate the result
     * @param <T>           The expected type of the response
     * @param <C>           The type of the container
     *
     * @return  Promise of the expected type
     */
    public abstract <T, C extends Collection> Promise<Collection<T>> dispatch(SerializedRequest request,
                                                                              Class<T> expectedType,
                                                                              Class<C> containerType);

    //===================================================================
    // Registrations
    //===================================================================

    /**
     * Register a {@link SerializationModule}.
     *
     * @param serializationModule  The module containing one or many generated serdes
     *
     * @return the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    public abstract HandlerRegistration register(SerializationModule serializationModule);
}
