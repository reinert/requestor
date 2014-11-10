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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.serialization.ContainerProviderManager;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.FormParamSerializer;
import io.reinert.requestor.serialization.HasImpls;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerdesManager;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonBooleanSerdes;
import io.reinert.requestor.serialization.json.JsonNumberSerdes;
import io.reinert.requestor.serialization.json.JsonStringSerdes;
import io.reinert.requestor.serialization.json.OverlaySerdes;
import io.reinert.requestor.serialization.misc.TextDeserializer;
import io.reinert.requestor.serialization.misc.VoidSerdes;

/**
 * Default implementation for {@link Requestor}.
 *
 * @author Danilo Reinert
 */
public class RequestorImpl implements Requestor {

    private static GeneratedJsonSerdes generatedJsonSerdes;

    private final SerdesManager serdesManager = new SerdesManager();
    private final FilterManager filterManager = new FilterManager();
    private final ContainerProviderManager collectionFactoryManager = new ContainerProviderManager();
    private String defaultContentType = "application/json";

    public RequestorImpl() {
        initSerdesManager();
    }

    //===================================================================
    // Request factory methods
    //===================================================================

    @Override
    public RequestDispatcher request(String uri) {
        return createRequest(uri);
    }

    //===================================================================
    // Requestor configuration
    //===================================================================

    @Override
    public void setDefaultContentType(String contentType) {
        this.defaultContentType = contentType;
    }

    @Override
    public String getDefaultContentType() {
        return defaultContentType;
    }

    @Override
    public <T> Deserializer<T> getDeserializer(Class<T> type, String contentType) {
        return serdesManager.getDeserializer(type, contentType);
    }

    @Override
    public <T> Serializer<T> getSerializer(Class<T> type, String contentType) {
        return serdesManager.getSerializer(type, contentType);
    }

    @Override
    public <T> HandlerRegistration putDeserializer(Class<T> type, Deserializer<T> deserializer) {
        return serdesManager.putDeserializer(type, deserializer);
    }

    @Override
    public <T> HandlerRegistration putSerializer(Class<T> type, Serializer<T> serializer) {
        return serdesManager.putSerializer(type, serializer);
    }

    @Override
    public <T> HandlerRegistration putSerdes(Class<T> type, Serdes<T> serdes) {
        return serdesManager.putSerdes(type, serdes);
    }

    @Override
    public HandlerRegistration addRequestFilter(RequestFilter requestFilter) {
        return filterManager.addRequestFilter(requestFilter);
    }

    @Override
    public HandlerRegistration addResponseFilter(ResponseFilter responseFilter) {
        return filterManager.addResponseFilter(responseFilter);
    }

    @Override
    public <C extends Collection> HandlerRegistration putContainerProvider(Class<C> collectionType,
                                                                           Provider<C> factory) {
        return collectionFactoryManager.putProvider(collectionType, factory);
    }

    private RequestDispatcher createRequest(String uri) {
        final RequestImpl request = new RequestImpl(uri, serdesManager, collectionFactoryManager, filterManager);
        request.contentType(defaultContentType);
        request.accept(defaultContentType);
        return request;
    }

    private void initSerdesManager() {
        serdesManager.putSerdes(String.class, JsonStringSerdes.getInstance());
        serdesManager.putSerdes(Number.class, JsonNumberSerdes.getInstance());
        serdesManager.putSerdes(Boolean.class, JsonBooleanSerdes.getInstance());
        serdesManager.putSerdes(Void.class, VoidSerdes.getInstance());
        serdesManager.putSerdes(JavaScriptObject.class, OverlaySerdes.getInstance());
        serdesManager.putDeserializer(String.class, TextDeserializer.getInstance());
        serdesManager.putSerializer(FormParam.class, FormParamSerializer.getInstance());

        initGeneratedJsonSerdes();

        for (Serdes<?> serdes : generatedJsonSerdes) {
            final Class handledType = serdes.handledType();
            serdesManager.putSerdes(handledType, serdes);
            if (serdes instanceof HasImpls) {
                HasImpls hasImpls = (HasImpls) serdes;
                for (Class impl : hasImpls.implTypes()) {
                    serdesManager.putSerdes(impl, serdes);
                }
            }
        }
    }

    private void initGeneratedJsonSerdes() {
        if (generatedJsonSerdes == null)
            generatedJsonSerdes = GWT.create(GeneratedJsonSerdes.class);
    }
}
