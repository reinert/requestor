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
package io.reinert.requestor;

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

/**
 * This interface is a configurable container responsible for building requests.
 * Usually, you will use it as a singleton.
 * <p></p>
 *
 * It provides a convenience API for managing/creating HTTP Requests.
 * <p></p>
 *
 * You can register {@link RequestFilter} or {@link ResponseFilter}, to manipulate all your requests.
 * <p></p>
 *
 * You can register {@link RequestInterceptor} or {@link ResponseInterceptor} to intercept every request/response and
 * modify their payloads.
 * <p></p>
 *
 * You can register {@link Serializer} or a {@link Deserializer} to provide both serialization and/or deserialization
 * of objects according to media-types.
 *
 * You can quickly send requests using the HTTP methods.
 *
 * @author Danilo Reinert
 */
public abstract class Requestor implements SerializerManager, FilterManager, InterceptorManager, ProviderManager,
        DirectInvoker, RequestDefaults {

    public static Requestor newInstance() {
        return GWT.create(Requestor.class);
    }

    //===================================================================
    // Requestor configuration
    //===================================================================

    public abstract Store getStore();

    public abstract void clearStore();

    public abstract <T> T getInstance(Class<T> type);

    public abstract <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType);

    public abstract <T> Serializer<T> getSerializer(Class<T> type, String mediaType);

    public abstract <T> Provider<T> getProvider(Class<T> type);

    /**
     * Register a {@link SerializationModule}.
     *
     * @param serializationModule  The module containing one or many generated serializer
     *
     * @return The {@link Registration} object, capable of cancelling this registration
     */
    public abstract Registration register(SerializationModule serializationModule);

    /**
     * A client service useful to communicate with REST like resources.
     *
     * @param resourceUri   Base URI of the resource
     * @param resourceType  Class of the resource
     * @param idType        Class of the resource's ID
     * @param collectionType Class in which you want to accumulate collection results
     * @param <R>           Resource type
     * @param <I>           Resource's ID type
     * @param <C>           Container type
     * @return              A ResourceService of the Resource Type
     */
    public abstract <R, I, C extends Collection> RestService<R, I, C> newRestService(String resourceUri,
                                                                                     Class<R> resourceType,
                                                                                     Class<I> idType,
                                                                                     Class<C> collectionType);

    //===================================================================
    // Request factory methods
    //===================================================================

    /**
     * Start building a request with the given uri string.
     *
     * @param uri   The uri of the request.
     *
     * @return  The request builder.
     */
    public abstract RequestInvoker req(String uri);

    /**
     * Start building a request with the given Uri.
     *
     * @param uri   The uri of the request.
     *
     * @return  The request builder.
     */
    public abstract RequestInvoker req(Uri uri);

    /**
     * Start building a request with the given Link.
     *
     * @param link   The link to request.
     *
     * @return  The request builder.
     */
    public abstract RequestInvoker req(Link link);

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

    protected abstract RequestDefaultsImpl getDefaults();

}
