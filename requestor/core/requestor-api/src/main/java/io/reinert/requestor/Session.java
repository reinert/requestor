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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.header.Header;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

/**
 * This is a configurable client session responsible for building requests.
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
public abstract class Session implements SerializerManager, FilterManager, InterceptorManager, ProviderManager,
        DirectInvoker, RequestDefaults {

    private final RequestDefaultsImpl defaults = new RequestDefaultsImpl();
    private final SessionStore store = new SessionStore();
    private final SerializerManagerImpl serializerManager = new SerializerManagerImpl();
    private final ProviderManagerImpl providerManager = new ProviderManagerImpl();
    private final FilterManagerImpl filterManager = new FilterManagerImpl();
    private final InterceptorManagerImpl interceptorManager = new InterceptorManagerImpl();
    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final SerializationEngine serializationEngine;
    private final RequestDispatcher.Factory requestDispatcherFactory;
    private final Deferred.Factory deferredFactory;

    public Session() {
        this(GWT.<Deferred.Factory>create(Deferred.Factory.class));
    }

    public Session(Deferred.Factory deferredFactory) {
        this(deferredFactory, new XhrRequestDispatcherFactory());
    }

    public Session(Deferred.Factory deferredFactory, RequestDispatcher.Factory requestDispatcherFactory) {
        this.requestDispatcherFactory = requestDispatcherFactory;
        this.deferredFactory = deferredFactory;

        // init processors
        serializationEngine = new SerializationEngine(serializerManager, providerManager);
        requestProcessor = new RequestProcessor(serializationEngine, defaults.getRequestSerializer(), filterManager,
                interceptorManager);
        responseProcessor = new ResponseProcessor(serializationEngine, defaults.getResponseDeserializer(),
                filterManager, interceptorManager);

        // register generated serializer to the session
        GeneratedModulesBinder.bind(serializerManager, providerManager);

        // perform initial set-up by implementations
        configure();
    }

    /**
     * Perform initial configuration for the session in construction.
     */
    protected abstract void configure();

    //===================================================================
    // Request methods
    //===================================================================

    /**
     * Start building a request with the given uri string.
     *
     * @param uri   The uri of the request.
     *
     * @return  The request builder.
     */
    public RequestInvoker req(String uri) {
        if (uri == null) throw new IllegalArgumentException("Uri string cannot be null.");
        return createRequest(Uri.create(uri));
    }

    /**
     * Start building a request with the given Uri.
     *
     * @param uri   The uri of the request.
     *
     * @return  The request builder.
     */
    public RequestInvoker req(Uri uri) {
        if (uri == null) throw new IllegalArgumentException("Uri cannot be null.");
        return createRequest(uri);
    }

    /**
     * Start building a request with the given Link.
     *
     * @param link   The link to request.
     *
     * @return  The request builder.
     */
    public RequestInvoker req(Link link) {
        if (link == null) throw new IllegalArgumentException("Link cannot be null.");
        return createRequest(link.getUri());
    }

    /**
     * Build a new web resource target.
     *
     * @param uri  Stringified URI of the target resource. May contain URI template parameters.
     *             Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public WebTarget target(String uri) {
        if (uri == null) throw new IllegalArgumentException("Uri string cannot be null.");
        return createWebTarget(UriBuilder.fromUri(uri));
    }

    /**
     * Build a new web resource target.
     *
     * @param uri  Web resource URI represented. Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public WebTarget target(Uri uri) {
        if (uri == null) throw new IllegalArgumentException("Uri cannot be null.");
        return createWebTarget(uri);
    }

    /**
     * Build a new web resource target.
     *
     * @param uriBuilder  Web resource URI represented as URI builder. Must not be {@code null}.
     *
     * @return  Web resource target bound to the provided URI.
     */
    public WebTarget target(UriBuilder uriBuilder) {
        if (uriBuilder == null) throw new IllegalArgumentException("UriBuilder cannot be null.");
        return createWebTarget(uriBuilder);
    }

    /**
     * Build a new web resource target.
     *
     * @param link  Link to a web resource. Must not be {@code null}.
     *
     * @return  Web resource target bound to the link web resource.
     */
    public WebTarget target(Link link) {
        if (link == null) throw new IllegalArgumentException("Link cannot be null.");
        return createWebTarget(link.getUri());
    }

    //===================================================================
    // Direct invoke methods
    //===================================================================

    @Override
    public Promise<Void> get(String uri) {
        return this.req(uri).get();
    }

    @Override
    public <T> Promise<T> get(String uri, Class<T> entityType) {
        return this.req(uri).get(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> get(String uri, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).get(collectionType, entityType);
    }

    @Override
    public Promise<Void> post(String uri) {
        return this.req(uri).post();
    }

    @Override
    public Promise<Void> post(String uri, Object payload) {
        return this.req(uri).payload(payload).post();
    }

    @Override
    public <T> Promise<T> post(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).post(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> post(String uri, Object payload, Class<C> collectionType,
                                                                 Class<T> entityType) {
        return this.req(uri).payload(payload).post(collectionType, entityType);
    }

    @Override
    public <T> Promise<T> post(String uri, Class<T> entityType) {
        return this.req(uri).post(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> post(String uri, Class<C> collectionType,
                                                                 Class<T> entityType) {
        return this.req(uri).post(collectionType, entityType);
    }

    @Override
    public Promise<Void> put(String uri) {
        return this.req(uri).put();
    }

    @Override
    public Promise<Void> put(String uri, Object payload) {
        return this.req(uri).payload(payload).put();
    }

    @Override
    public <T> Promise<T> put(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).put(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> put(String uri, Object payload, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).payload(payload).put(collectionType, entityType);
    }

    @Override
    public <T> Promise<T> put(String uri, Class<T> entityType) {
        return this.req(uri).put(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> put(String uri, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).put(collectionType, entityType);
    }

    @Override
    public Promise<Void> delete(String uri) {
        return this.req(uri).delete();
    }

    @Override
    public <T> Promise<T> delete(String uri, Class<T> entityType) {
        return this.req(uri).delete(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> delete(String uri, Class<C> collectionType,
                                                                   Class<T> entityType) {
        return this.req(uri).delete(collectionType, entityType);
    }

    @Override
    public Promise<Void> patch(String uri) {
        return this.req(uri).patch();
    }

    @Override
    public Promise<Void> patch(String uri, Object payload) {
        return this.req(uri).payload(payload).patch();
    }

    @Override
    public <T> Promise<T> patch(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).patch(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> patch(String uri, Object payload, Class<C> collectionType,
                                                                  Class<T> entityType) {
        return this.req(uri).payload(payload).patch(collectionType, entityType);
    }

    @Override
    public <T> Promise<T> patch(String uri, Class<T> entityType) {
        return this.req(uri).patch(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> patch(String uri, Class<C> collectionType,
                                                                  Class<T> entityType) {
        return this.req(uri).patch(collectionType, entityType);
    }

    @Override
    public Promise<Void> options(String uri) {
        return this.req(uri).options();
    }

    @Override
    public <T> Promise<T> options(String uri, Class<T> entityType) {
        return this.req(uri).options(entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> options(String uri, Class<C> collectionType,
                                                                    Class<T> entityType) {
        return this.req(uri).options(collectionType, entityType);
    }

    @Override
    public Promise<Headers> head(String uri) {
        return this.req(uri).head();
    }

    //===================================================================
    // Session configuration
    //===================================================================

    public Store getStore() {
        return store;
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        defaults.setRequestSerializer(requestSerializer);
        requestProcessor.setRequestSerializer(requestSerializer);
    }

    public RequestSerializer getRequestSerializer() {
        return defaults.getRequestSerializer();
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        defaults.setResponseDeserializer(responseDeserializer);
        responseProcessor.setResponseDeserializer(responseDeserializer);
    }

    public ResponseDeserializer getResponseDeserializer() {
        return defaults.getResponseDeserializer();
    }

    public <T> Serializer<T> getSerializer(Class<T> type, String mediaType) {
        return serializerManager.getSerializer(type, mediaType);
    }

    public <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType) {
        return serializerManager.getDeserializer(type, mediaType);
    }

    public <T> Provider<T> getProvider(Class<T> type) {
        return providerManager.get(type);
    }

    public <T> T getInstance(Class<T> type) {
        Provider<T> provider = providerManager.get(type);
        if (provider == null) {
            throw new RuntimeException("There's no Provider registered for this class.");
        }
        return provider.getInstance();
    }

    @Override
    public void reset() {
        defaults.reset();
    }

    @Override
    public void setMediaType(String mediaType) {
        defaults.setMediaType(mediaType);
    }

    @Override
    public String getMediaType() {
        return defaults.getMediaType();
    }

    @Override
    public void setAuth(Auth auth) {
        defaults.setAuth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        defaults.setAuth(authProvider);
    }

    @Override
    public Auth getAuth() {
        return defaults.getAuth();
    }

    @Override
    public Auth.Provider getAuthProvider() {
        return defaults.getAuthProvider();
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        defaults.setTimeout(timeoutMillis);
    }

    @Override
    public int getTimeout() {
        return defaults.getTimeout();
    }

    @Override
    public void setDelay(int delayMillis) {
        defaults.setDelay(delayMillis);
    }

    @Override
    public int getDelay() {
        return defaults.getDelay();
    }

    @Override
    public void setPolling(PollingStrategy strategy, int intervalMillis, int limit) {
        defaults.setPolling(strategy, intervalMillis, limit);
    }

    @Override
    public boolean isPolling() {
        return defaults.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return defaults.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return defaults.getPollingLimit();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return defaults.getPollingStrategy();
    }

    @Override
    public void putHeader(Header header) {
        defaults.putHeader(header);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        defaults.setHeader(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return defaults.getHeaders();
    }

    @Override
    public String getHeader(String headerName) {
        return defaults.getHeader(headerName);
    }

    @Override
    public Header popHeader(String headerName) {
        return defaults.popHeader(headerName);
    }

    /**
     * Register a {@link SerializationModule}.
     *
     * @param serializationModule  The module containing one or many generated serializer
     *
     * @return The {@link Registration} object, capable of cancelling this registration
     */
    public Registration register(SerializationModule serializationModule) {
        final List<Registration> registrations = new ArrayList<Registration>();

        if (serializationModule.getSerializers() != null) {
            for (Serializer<?> serializer : serializationModule.getSerializers()) {
                registrations.add(register(serializer));
            }
        }

        if (serializationModule.getTypeProviders() != null) {
            for (TypeProvider<?> provider : serializationModule.getTypeProviders()) {
                registrations.add(register(provider));
            }
        }

        return new Registration() {
            @Override
            public void cancel() {
                for (Registration registration : registrations) {
                    registration.cancel();
                }
            }
        };
    }

    @Override
    public Registration register(Deserializer<?> deserializer) {
        return serializerManager.register(deserializer);
    }

    @Override
    public Registration register(DeserializerProvider deserializerProvider) {
        return serializerManager.register(deserializerProvider);
    }

    @Override
    public Registration register(Serializer<?> serializer) {
        return serializerManager.register(serializer);
    }

    @Override
    public Registration register(SerializerProvider serializerProvider) {
        return serializerManager.register(serializerProvider);
    }

    @Override
    public Registration register(RequestFilter requestFilter) {
        return filterManager.register(requestFilter);
    }

    @Override
    public Registration register(RequestFilter.Provider provider) {
        return filterManager.register(provider);
    }

    @Override
    public Registration register(ResponseFilter responseFilter) {
        return filterManager.register(responseFilter);
    }

    @Override
    public Registration register(ResponseFilter.Provider provider) {
        return filterManager.register(provider);
    }

    @Override
    public Registration register(RequestInterceptor requestInterceptor) {
        return interceptorManager.register(requestInterceptor);
    }

    @Override
    public Registration register(RequestInterceptor.Provider provider) {
        return interceptorManager.register(provider);
    }

    @Override
    public Registration register(ResponseInterceptor responseInterceptor) {
        return interceptorManager.register(responseInterceptor);
    }

    @Override
    public Registration register(ResponseInterceptor.Provider provider) {
        return interceptorManager.register(provider);
    }

    @Override
    public <T> Registration register(Class<T> type, Provider<T> provider) {
        return providerManager.register(type, provider);
    }

    @Override
    public <T> Registration register(TypeProvider<T> provider) {
        return providerManager.register(provider);
    }

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
    public <R, I, C extends Collection> RestService<R, I, C> newRestService(String resourceUri,
                                                                            Class<R> resourceType,
                                                                            Class<I> idType,
                                                                            Class<C> collectionType) {
        return new RestService<R, I, C>(this, resourceUri, resourceType, idType, collectionType);
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected RequestDefaultsImpl getDefaults() {
        return defaults;
    }

    private RequestInvoker createRequest(Uri uri) {
        final RequestInvoker request = new RequestInvokerImpl(uri, new TransientStore(store),
                requestDispatcherFactory.newRequestDispatcher(requestProcessor, responseProcessor, deferredFactory));

        defaults.apply(request);

        return request;
    }

    private WebTarget createWebTarget(Uri uri) {
        return WebTarget.create(filterManager, interceptorManager, serializationEngine, requestDispatcherFactory,
                deferredFactory, store, defaults, uri);
    }

    private WebTarget createWebTarget(UriBuilder uriBuilder) {
        return WebTarget.create(filterManager, interceptorManager, serializationEngine, requestDispatcherFactory,
                deferredFactory, store, defaults, uriBuilder);
    }
}
