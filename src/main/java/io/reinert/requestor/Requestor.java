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

import com.google.web.bindery.event.shared.HandlerRegistration;

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
 * You can register {@link RequestFilter}s with #addRequestFilter, so the are executed over all your requests.
 * The same for {@link io.reinert.requestor.ResponseFilter}.
 * <p/>
 *
 * You can register custom {@link io.reinert.requestor.serialization.Serializer} with #registerSerializer.
 * The same for {@link io.reinert.requestor.serialization.Deserializer}.
 * If you want to support both serialization and deserialization for your custom object,
 * register a {@link io.reinert.requestor.serialization.Serdes} with #registerSerdes.
 * <p/>
 *
 * SerDes for {@link String}, {@link Number}, {@link Boolean}
 * and {@link com.google.gwt.core.client.JavaScriptObject} are already provided.
 *
 * @author Danilo Reinert
 */
public interface Requestor {

    //===================================================================
    // Requestor configuration
    //===================================================================

    void setDefaultMediaType(String contentType);

    String getDefaultMediaType();

    <T> Deserializer<T> getDeserializer(Class<T> type, String contentType);

    <T> Provider<T> getProvider(Class<T> type);

    <T> Serializer<T> getSerializer(Class<T> type, String contentType);

    /**
     * Register a collection Provider.
     *
     * @param type      The class of T
     * @param provider  The Provider of T
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration bindProvider(Class<T> type, Provider<T> provider);

    /**
     * Register a deserializer of the given type.
     *
     * @param deserializer  The deserializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration addDeserializer(Deserializer<T> deserializer);

    /**
     * Register a request filter.
     *
     * @param requestFilter The request filter to be registered.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    HandlerRegistration addRequestFilter(RequestFilter requestFilter);

    /**
     * Register a response filter.
     *
     * @param responseFilter The response filter to be registered.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    HandlerRegistration addResponseFilter(ResponseFilter responseFilter);

    /**
     * Register a request interceptor.
     *
     * @param requestInterceptor The request interceptor to be registered.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    HandlerRegistration addRequestInterceptor(RequestInterceptor requestInterceptor);

    /**
     * Register a response interceptor.
     *
     * @param responseInterceptor The response interceptor to be registered.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    HandlerRegistration addResponseInterceptor(ResponseInterceptor responseInterceptor);

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
     * Create a {@link RequestBuilder} with the passed url.
     *
     * @param url   The url of the request.
     *
     * @return The {@link RequestBuilder} instance.
     */
    RequestInvoker request(String url);
}
