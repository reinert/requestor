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
import io.reinert.requestor.serialization.Serdes;
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

    private final SerdesManager serdesManager = new SerdesManager();
    private final FilterManager filterManager = new FilterManager();
    private final InterceptorManager interceptorManager = new InterceptorManager();
    private final ProviderManager providerManager = new ProviderManager();
    private final RequestDispatcherFactory requestDispatcherFactory = GWT.create(RequestDispatcherFactory.class);
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;

    private String defaultMediaType = "application/json";

    public RequestorImpl() {
        initSerdesManager();
        initProcessors();
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
    public void setDefaultMediaType(String mediaType) {
        this.defaultMediaType = mediaType;
    }

    @Override
    public String getDefaultMediaType() {
        return defaultMediaType;
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
    public HandlerRegistration addRequestInterceptor(RequestInterceptor requestInterceptor) {
        return interceptorManager.addRequestInterceptor(requestInterceptor);
    }

    @Override
    public HandlerRegistration addResponseInterceptor(ResponseInterceptor responseInterceptor) {
        return interceptorManager.addResponseInterceptor(responseInterceptor);
    }

    @Override
    public <T> HandlerRegistration bindProvider(Class<T> type, Provider<T> factory) {
        return providerManager.bind(type, factory);
    }

    private RequestInvoker createRequest(String uri) {
        final AbstractRequestInvoker request = new RequestInvokerImpl(uri, requestProcessor,
                requestDispatcherFactory.getRequestDispatcher(responseProcessor));
        request.contentType(defaultMediaType);
        request.accept(defaultMediaType);
        return request;
    }

    private void initSerdesManager() {
        serdesManager.addSerdes(JsonStringSerdes.getInstance());
        serdesManager.addSerdes(JsonNumberSerdes.getInstance());
        serdesManager.addSerdes(JsonBooleanSerdes.getInstance());
        serdesManager.addSerdes(VoidSerdes.getInstance());
        serdesManager.addSerdes(OverlaySerdes.getInstance());
        serdesManager.addSerdes(TextSerdes.getInstance());
        GeneratedJsonSerdesBinder.bind(serdesManager, providerManager);
    }

    private void initProcessors() {
        final FilterEngine filterEngine = new FilterEngine(filterManager);
        final SerializationEngine serializationEngine = new SerializationEngine(serdesManager, providerManager);
        final InterceptorEngine interceptorEngine = new InterceptorEngine(interceptorManager);
        requestProcessor = new RequestProcessor(serializationEngine, filterEngine, interceptorEngine);
        responseProcessor = new ResponseProcessor(serializationEngine, filterEngine, interceptorEngine);
    }
}
