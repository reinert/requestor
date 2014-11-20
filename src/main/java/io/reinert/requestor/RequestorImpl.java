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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.FormParamSerializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerdesManager;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonBooleanSerdes;
import io.reinert.requestor.serialization.json.JsonNumberSerdes;
import io.reinert.requestor.serialization.json.JsonStringSerdes;
import io.reinert.requestor.serialization.json.OverlaySerdes;
import io.reinert.requestor.serialization.misc.TextSerdes;
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
    private final ProviderManager providerManager = new ProviderManager();
    private final RequestDispatcherFactory requestDispatcherFactory = GWT.create(RequestDispatcherFactory.class);
    private FilterEngine filterEngine;
    private SerializationEngine serializationEngine;

    private String defaultContentType = "application/json";

    public RequestorImpl() {
        initSerdesManager();
    }

    //===================================================================
    // Request factory methods
    //===================================================================

    @Override
    public RequestInvoker request(String url) {
        return createRequest(url);
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
    public <T> Provider<T> getProvider(Class<T> type) {
        return providerManager.get(type);
    }

    @Override
    public <T> Serializer<T> getSerializer(Class<T> type, String contentType) {
        return serdesManager.getSerializer(type, contentType);
    }

    @Override
    public <T> HandlerRegistration addDeserializer(Deserializer<T> deserializer) {
        return serdesManager.addDeserializer(deserializer);
    }

    @Override
    public <T> HandlerRegistration addSerializer(Serializer<T> serializer) {
        return serdesManager.addSerializer(serializer);
    }

    @Override
    public <T> HandlerRegistration addSerdes(Serdes<T> serdes) {
        return serdesManager.addSerdes(serdes);
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
    public <T> HandlerRegistration bindProvider(Class<T> type, Provider<T> factory) {
        return providerManager.bind(type, factory);
    }

    private RequestInvoker createRequest(String uri) {
        final RequestImpl request = new RequestImpl(uri, serializationEngine, filterEngine);
        request.contentType(defaultContentType);
        request.accept(defaultContentType);
        return request;
    }

    private void initSerdesManager() {
        serdesManager.addSerdes(JsonStringSerdes.getInstance());
        serdesManager.addSerdes(JsonNumberSerdes.getInstance());
        serdesManager.addSerdes(JsonBooleanSerdes.getInstance());
        serdesManager.addSerdes(VoidSerdes.getInstance());
        serdesManager.addSerdes(OverlaySerdes.getInstance());
        serdesManager.addSerdes(TextSerdes.getInstance());
        serdesManager.addSerializer(FormParamSerializer.getInstance());

        initGeneratedJsonSerdes();

        for (Serdes<?> serdes : generatedJsonSerdes.getGeneratedSerdes()) {
            serdesManager.addSerdes(serdes);
        }
        for (GeneratedProvider provider : generatedJsonSerdes.getGeneratedProviders()) {
            providerManager.bind(provider.getType(), provider);
        }

        filterEngine = new FilterEngine(filterManager);
        serializationEngine = new SerializationEngine(serdesManager, providerManager);
    }

    private void initGeneratedJsonSerdes() {
        if (generatedJsonSerdes == null)
            generatedJsonSerdes = GWT.create(GeneratedJsonSerdes.class);
    }
}
