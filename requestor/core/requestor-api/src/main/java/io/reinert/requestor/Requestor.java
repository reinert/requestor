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

import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.deferred.Promise;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;

/**
 * This interface is a configurable {@link Request} Provider.
 * Usually, you will use it as a singleton along your project.
 * <p/>
 *
 * It provides a convenience API for managing/creating HTTP Requests.
 * <p/>
 *
 * You can register {@link RequestFilter}s with #addRequestFilter, so they are executed over all your requests.
 * The same for {@link ResponseFilter}.
 * <p/>
 *
 * You can register {@link RequestInterceptor}s with #addRequestInterceptor to intercept every ongoing request.
 * As well you can register {@link ResponseInterceptor}s with #addResponseInterceptor to intercept every incoming
 * response.
 * <p/>
 *
 * You can register custom {@link io.reinert.requestor.serialization.Serializer} with #registerSerializer.
 * The same for {@link Deserializer}.
 * If you want to support both serialization and deserialization for your custom object,
 * register a {@link Serdes} with #registerSerdes.
 * <p/>
 *
 * SerDes for {@link String}, {@link Number}, {@link Boolean}
 * and {@link com.google.gwt.core.client.JavaScriptObject} are already provided.
 *
 * @author Danilo Reinert
 */
public interface Requestor extends HasFilters, HasInterceptors {

    //===================================================================
    // Requestor configuration
    //===================================================================

    void setDefaultMediaType(String contentType);

    String getDefaultMediaType();

    <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType);

    <T> Serializer<T> getSerializer(Class<T> type, String mediaType);

    <T> Provider<T> getProvider(Class<T> type);

    <T> T getInstance(Class<T> type);

    /**
     * Register a collection Provider.
     *
     * @param type      The class of T
     * @param provider  The Provider of T
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration bindProvider(Class<T> type, Provider<? extends T> provider);

    /**
     * Register a deserializer of the given type.
     *
     * @param deserializer  The deserializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration addDeserializer(Deserializer<T> deserializer);

    /**
     * Register a serializer/deserializer of the given type.
     *
     * @param serdes    The serializer/deserializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration addSerdes(Serdes<T> serdes);

    /**
     * Register a serializer of the given type.
     *
     * @param serializer  The serializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration addSerializer(Serializer<T> serializer);

    //===================================================================
    // Request factory methods
    //===================================================================

    /**
     * Create a {@link RequestInvoker} with the given url.
     *
     * @param url   The url of the request.
     *
     * @return  The request builder.
     */
    RequestInvoker req(String url);

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
    <T> Promise<T> dispatch(SerializedRequest request, Class<T> expectedType);

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
    <T, C extends Collection> Promise<Collection<T>> dispatch(SerializedRequest request, Class<T> expectedType,
                                                              Class<C> containerType);
}
