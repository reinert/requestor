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
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.deferred.Promise;
import io.reinert.requestor.form.FormDataSerializer;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;

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
    private final RequestDispatcher requestDispatcher;
    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;

    private String defaultMediaType;

    public RequestorImpl() {
        this(GWT.<FormDataSerializer>create(FormDataSerializer.class));
    }

    public RequestorImpl(FormDataSerializer formDataSerializer) {
        this(formDataSerializer,
             GWT.<RequestDispatcherFactory>create(RequestDispatcherFactory.class),
             GWT.<DeferredFactory>create(DeferredFactory.class));
    }

    public RequestorImpl(FormDataSerializer formDataSerializer, RequestDispatcherFactory requestDispatcherFactory,
                         DeferredFactory deferredFactory) {
        // init processors
        final FilterEngine filterEngine = new FilterEngine(filterManager);
        final SerializationEngine serializationEngine = new SerializationEngine(serdesManager, providerManager);
        final InterceptorEngine interceptorEngine = new InterceptorEngine(interceptorManager);
        requestProcessor = new RequestProcessor(serializationEngine, filterEngine, interceptorEngine,
                formDataSerializer);
        responseProcessor = new ResponseProcessor(serializationEngine, filterEngine, interceptorEngine);

        // init dispatcher
        requestDispatcher = requestDispatcherFactory.getRequestDispatcher(responseProcessor, deferredFactory);

        // bind generated serdes to the requestor
        GeneratedJsonSerdesBinder.bind(serdesManager, providerManager);

        // perform initial set-up by user
        GWT.<RequestorInitializer>create(RequestorInitializer.class).configure(this);
    }

    //===================================================================
    // Request methods
    //===================================================================

    @Override
    public RequestSender req(String url) {
        return createRequest(url);
    }

    public <T> Promise<T> dispatch(SerializedRequest request, Class<T> returnType) {
        return requestDispatcher.dispatch(request, returnType);
    }

    public <T, C extends Collection> Promise<Collection<T>> dispatch(SerializedRequest request, Class<T> returnType,
                                                                     Class<C> containerType) {
        return requestDispatcher.dispatch(request, returnType, containerType);
    }

    //===================================================================
    // Requestor configuration
    //===================================================================

    @Override
    public void setDefaultMediaType(String mediaType) {
        if (mediaType != null) {
            int i = mediaType.indexOf('/');
            if (i == -1 || i != mediaType.lastIndexOf('/'))
                throw new IllegalArgumentException("Media-type must follow the pattern {type}/{subtype}");
        }
        this.defaultMediaType = mediaType;
    }

    @Override
    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    @Override
    public <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType) {
        return serdesManager.getDeserializer(type, mediaType);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return providerManager.get(type);
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        Provider<T> provider = providerManager.get(type);
        if (provider == null)
            throw new RuntimeException("There's no Provider registered for this class.");
        return provider.get();
    }

    @Override
    public <T> Serializer<T> getSerializer(Class<T> type, String mediaType) {
        return serdesManager.getSerializer(type, mediaType);
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
    public <T> HandlerRegistration bindProvider(Class<T> type, Provider<? extends T> factory) {
        return providerManager.bind(type, factory);
    }

    private RequestSender createRequest(String uri) {
        final RequestSender request = new RequestSenderImpl(uri, requestProcessor, requestDispatcher);
        if (defaultMediaType != null) {
            request.contentType(defaultMediaType);
            request.accept(defaultMediaType);
        }
        return request;
    }
}
