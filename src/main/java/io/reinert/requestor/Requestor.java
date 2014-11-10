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

    void setDefaultContentType(String contentType);

    String getDefaultContentType();

    <T> Deserializer<T> getDeserializer(Class<T> type, String contentType);

    <T> Serializer<T> getSerializer(Class<T> type, String contentType);

    /**
     * Register a collection Provider.
     *
     * @param collectionType    The class of the collection
     * @param provider           The Provider of the collection
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <C extends Collection> HandlerRegistration putContainerProvider(Class<C> collectionType, Provider<C> provider);

    /**
     * Register a deserializer of the given type.
     *
     * @param type          The target type of the deserializer.
     * @param deserializer  The deserializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration putDeserializer(Class<T> type, Deserializer<T> deserializer);

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
     * Register a serializer/deserializer of the given type.
     *
     * @param type      The target type of the serializer/deserializer.
     * @param serdes    The serializer/deserializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration putSerdes(Class<T> type, Serdes<T> serdes);

    /**
     * Register a serializer of the given type.
     *
     * @param type        The target type of the serializer.
     * @param serializer  The serializer of T.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this registration.
     */
    <T> HandlerRegistration putSerializer(Class<T> type, Serializer<T> serializer);

    //===================================================================
    // Request factory methods
    //===================================================================

    /**
     * Create a {@link Request} of no request/response content.
     *
     * @return The FluentRequest with void request and response contents.
     */
    RequestDispatcher request(String uri);
}
