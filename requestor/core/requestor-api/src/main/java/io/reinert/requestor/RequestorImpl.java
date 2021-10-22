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

import io.reinert.requestor.form.FormDataSerializer;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

/**
 * Default implementation for {@link Requestor}.
 *
 * @author Danilo Reinert
 */
public class RequestorImpl extends Requestor {

    private final SerdesManagerImpl serdesManager = new SerdesManagerImpl();
    private final ProviderManagerImpl providerManager = new ProviderManagerImpl();
    private final FilterManagerImpl filterManager = new FilterManagerImpl();
    private final InterceptorManagerImpl interceptorManager = new InterceptorManagerImpl();
    private final SerializationEngine serializationEngine;
    private final FormDataSerializer formDataSerializer;
    private final RequestDispatcherFactory requestDispatcherFactory;
    private final DeferredFactory deferredFactory;
    private final RequestDispatcher requestDispatcher;
    private final RequestProcessor requestProcessor;

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
        this.formDataSerializer = formDataSerializer;
        this.requestDispatcherFactory = requestDispatcherFactory;
        this.deferredFactory = deferredFactory;

        // init processor
        serializationEngine = new SerializationEngine(serdesManager, providerManager);
        final FilterEngine filterEngine = new FilterEngine(filterManager);
        final InterceptorEngine interceptorEngine = new InterceptorEngine(interceptorManager);
        requestProcessor = new RequestProcessor(serializationEngine, filterEngine, interceptorEngine,
                formDataSerializer);

        // init dispatcher
        final ResponseProcessor responseProcessor = new ResponseProcessor(serializationEngine, filterEngine,
                interceptorEngine);
        requestDispatcher = requestDispatcherFactory.getRequestDispatcher(responseProcessor, deferredFactory);

        // register generated serdes to the requestor
        GeneratedJsonSerdesBinder.bind(serdesManager, providerManager);

        // perform initial set-up by user
        GWT.<RequestorInitializer>create(RequestorInitializer.class).configure(this);
    }

    //===================================================================
    // Request methods
    //===================================================================

    @Override
    public RequestInvoker req(String uri) {
        return createRequest(Uri.create(uri));
    }

    @Override
    public RequestInvoker req(Uri uri) {
        return createRequest(uri);
    }

    @Override
    public WebTarget target(String uri) {
        if (uri == null)
            throw new NullPointerException("Uri string cannot be null.");
        return createWebTarget(UriBuilder.fromUri(uri));
    }

    @Override
    public WebTarget target(Uri uri) {
        if (uri == null)
            throw new NullPointerException("Uri cannot be null.");
        return createWebTarget(uri);
    }

    @Override
    public WebTarget target(UriBuilder uriBuilder) {
        if (uriBuilder == null)
            throw new NullPointerException("UriBuilder cannot be null.");
        return createWebTarget(uriBuilder);
    }

    @Override
    public WebTarget target(Link link) {
        if (link == null)
            throw new NullPointerException("Link cannot be null.");
        return createWebTarget(link.getUri());
    }

    @Override
    public <T> Promise<T> dispatch(SerializedRequest request, Class<T> returnType) {
        return requestDispatcher.dispatch(request, returnType);
    }

    @Override
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
            if (i == -1 || i != mediaType.lastIndexOf('/')) {
                throw new IllegalArgumentException("Media-type must follow the pattern {type}/{subtype}");
            }
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
        return provider.getInstance();
    }

    @Override
    public <T> Serializer<T> getSerializer(Class<T> type, String mediaType) {
        return serdesManager.getSerializer(type, mediaType);
    }

    @Override
    public <T> HandlerRegistration register(Deserializer<T> deserializer) {
        return serdesManager.register(deserializer);
    }

    @Override
    public <T> HandlerRegistration register(Serializer<T> serializer) {
        return serdesManager.register(serializer);
    }

    @Override
    public <T> HandlerRegistration register(Serdes<T> serdes) {
        return serdesManager.register(serdes);
    }

    @Override
    public HandlerRegistration register(RequestFilter requestFilter) {
        return filterManager.register(requestFilter);
    }

    @Override
    public HandlerRegistration register(ResponseFilter responseFilter) {
        return filterManager.register(responseFilter);
    }

    @Override
    public HandlerRegistration register(RequestInterceptor requestInterceptor) {
        return interceptorManager.register(requestInterceptor);
    }

    @Override
    public HandlerRegistration register(ResponseInterceptor responseInterceptor) {
        return interceptorManager.register(responseInterceptor);
    }

    @Override
    public HandlerRegistration register(Provider<?> provider) {
        return providerManager.register(provider);
    }

    @Override
    public HandlerRegistration register(SerializationModule serializationModule) {
        final int length = serializationModule.getSerdes().size() + serializationModule.getProviders().size();
        final HandlerRegistration[] registrations = new HandlerRegistration[length];
        int i = -1;

        for (Serdes<?> serdes : serializationModule.getSerdes()) {
            registrations[++i] = register(serdes);
        }

        for (Provider<?> provider : serializationModule.getProviders()) {
            registrations[++i] = register(provider);
        }

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                for (HandlerRegistration registration : registrations) {
                    registration.removeHandler();
                }
            }
        };
    }

    private RequestInvoker createRequest(Uri uri) {
        final RequestInvoker request = new RequestInvokerImpl(uri, requestProcessor, requestDispatcher);
        if (defaultMediaType != null) {
            request.contentType(defaultMediaType);
            request.accept(defaultMediaType);
        }
        return request;
    }

    private WebTarget createWebTarget(Uri uri) {
        return new WebTarget(filterManager, interceptorManager, serializationEngine, formDataSerializer,
                requestDispatcherFactory, deferredFactory, uri);
    }

    private WebTarget createWebTarget(UriBuilder uriBuilder) {
        return new WebTarget(filterManager, interceptorManager, serializationEngine, formDataSerializer,
                requestDispatcherFactory, deferredFactory, uriBuilder);
    }
}
