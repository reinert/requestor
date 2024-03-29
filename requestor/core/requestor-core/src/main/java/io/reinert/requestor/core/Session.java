/*
 * Copyright 2014-2022 Danilo Reinert
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
package io.reinert.requestor.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reinert.requestor.core.deferred.DeferredPoolFactoryImpl;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.internal.Reflection;
import io.reinert.requestor.core.serialization.Deserializer;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.uri.Uri;

/**
 * <p>This is a configurable client session responsible for building requests.
 * Usually, you will use it as a singleton.</p>
 *
 * <p>It provides a convenience API for managing/creating HTTP Requests.</p>
 *
 * <p>You can register {@link RequestFilter} or {@link ResponseFilter}, to manipulate all your requests.</p>
 *
 * <p>You can register {@link RequestInterceptor} or {@link ResponseInterceptor} to intercept every request/response and
 * modify their payloads.</p>
 *
 * <p>You can register {@link Serializer} or a {@link Deserializer} to provide both serialization and/or deserialization
 * of objects according to media-types.</p>
 *
 * <p>You can quickly send requests using the HTTP methods.</p>
 *
 * @author Danilo Reinert
 */
public class Session implements SerializerManager, FilterManager, InterceptorManager, ProviderManager,
        DirectInvoker, HasRequestOptions, Store {

    private final RequestLogger logger = new RequestLogger();
    private final RequestOptionsHolder options = new RequestOptionsHolder();
    private final RootStore store;
    private final SerializerManagerImpl serializerManager = new SerializerManagerImpl();
    private final ProviderManagerImpl providerManager = new ProviderManagerImpl();
    private final FilterManagerImpl filterManager = new FilterManagerImpl();
    private final InterceptorManagerImpl interceptorManager = new InterceptorManagerImpl();
    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final SerializationEngine serializationEngine;
    private final RequestDispatcher.Factory requestDispatcherFactory;
    private final DeferredPool.Factory deferredPoolFactory;
    private final AsyncRunner asyncRunner;

    public Session(AsyncRunner asyncRunner, RequestDispatcher.Factory requestDispatcherFactory) {
        this(asyncRunner, requestDispatcherFactory, new DeferredPoolFactoryImpl());
    }

    public Session(AsyncRunner asyncRunner, RequestDispatcher.Factory requestDispatcherFactory,
                   DeferredPool.Factory deferredPoolFactory) {
        this(asyncRunner, requestDispatcherFactory, deferredPoolFactory, new BaseRequestSerializer(),
                new BaseResponseDeserializer());
    }

    public Session(AsyncRunner asyncRunner, RequestDispatcher.Factory requestDispatcherFactory,
                   DeferredPool.Factory deferredPoolFactory, RequestSerializer requestSerializer,
                   ResponseDeserializer responseDeserializer) {
        if (asyncRunner == null) {
            throw new IllegalArgumentException("AsyncRunner cannot be null");
        }
        this.asyncRunner = asyncRunner;
        this.store = new RootStore(asyncRunner);

        if (requestDispatcherFactory == null) {
            throw new IllegalArgumentException("RequestDispatcher.Factory cannot be null");
        }
        this.requestDispatcherFactory = requestDispatcherFactory;

        if (deferredPoolFactory == null) {
            throw new IllegalArgumentException("Deferred.Factory cannot be null");
        }
        this.deferredPoolFactory = deferredPoolFactory;

        // init processors
        serializationEngine = new SerializationEngine(serializerManager, providerManager);
        requestProcessor = new RequestProcessor(serializationEngine, requestSerializer, filterManager,
                interceptorManager);
        responseProcessor = new ResponseProcessor(serializationEngine, responseDeserializer,
                filterManager, interceptorManager);
    }

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

    //===================================================================
    // Direct invoke methods
    //===================================================================

    @Override
    public Request<Void> get(String uri) {
        return this.req(uri).get();
    }

    @Override
    public <T> Request<T> get(String uri, Class<T> entityType) {
        return this.req(uri).get(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> get(String uri, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).get(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> get(String uri, Class<M> mapType, Class<K> keyType,
                                                        Class<V> valueType) {
        return this.req(uri).get(mapType, keyType, valueType);
    }

    @Override
    public Request<Void> post(String uri) {
        return this.req(uri).post();
    }

    @Override
    public Request<Void> post(String uri, Object payload) {
        return this.req(uri).payload(payload).post();
    }

    @Override
    public <T> Request<T> post(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).post(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> post(String uri, Object payload, Class<C> collectionType,
                                                                 Class<T> entityType) {
        return this.req(uri).payload(payload).post(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> post(String uri, Object payload, Class<M> mapType, Class<K> keyType,
                                                         Class<V> valueType) {
        return this.req(uri).payload(payload).post(mapType, keyType, valueType);
    }

    @Override
    public <T> Request<T> post(String uri, Class<T> entityType) {
        return this.req(uri).post(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> post(String uri, Class<C> collectionType,
                                                                 Class<T> entityType) {
        return this.req(uri).post(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> post(String uri, Class<M> mapType, Class<K> keyType,
                                                         Class<V> valueType) {
        return this.req(uri).post(mapType, keyType, valueType);
    }

    @Override
    public Request<Void> put(String uri) {
        return this.req(uri).put();
    }

    @Override
    public Request<Void> put(String uri, Object payload) {
        return this.req(uri).payload(payload).put();
    }

    @Override
    public <T> Request<T> put(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).put(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> put(String uri, Object payload, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).payload(payload).put(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> put(String uri, Object payload, Class<M> mapType, Class<K> keyType,
                                                        Class<V> valueType) {
        return this.req(uri).payload(payload).put(mapType, keyType, valueType);
    }

    @Override
    public <T> Request<T> put(String uri, Class<T> entityType) {
        return this.req(uri).put(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> put(String uri, Class<C> collectionType,
                                                                Class<T> entityType) {
        return this.req(uri).put(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> put(String uri, Class<M> mapType, Class<K> keyType,
                                                        Class<V> valueType) {
        return this.req(uri).put(mapType, keyType, valueType);
    }

    @Override
    public Request<Void> delete(String uri) {
        return this.req(uri).delete();
    }

    @Override
    public <T> Request<T> delete(String uri, Class<T> entityType) {
        return this.req(uri).delete(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> delete(String uri, Class<C> collectionType,
                                                                   Class<T> entityType) {
        return this.req(uri).delete(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> delete(String uri, Class<M> mapType, Class<K> keyType,
                                                           Class<V> valueType) {
        return this.req(uri).delete(mapType, keyType, valueType);
    }

    @Override
    public Request<Void> patch(String uri) {
        return this.req(uri).patch();
    }

    @Override
    public Request<Void> patch(String uri, Object payload) {
        return this.req(uri).payload(payload).patch();
    }

    @Override
    public <T> Request<T> patch(String uri, Object payload, Class<T> entityType) {
        return this.req(uri).payload(payload).patch(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> patch(String uri, Object payload, Class<C> collectionType,
                                                                  Class<T> entityType) {
        return this.req(uri).payload(payload).patch(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> patch(String uri, Object payload, Class<M> mapType,
                                                          Class<K> keyType, Class<V> valueType) {
        return this.req(uri).payload(payload).patch(mapType, keyType, valueType);
    }

    @Override
    public <T> Request<T> patch(String uri, Class<T> entityType) {
        return this.req(uri).patch(entityType);
    }

    @Override
    public <T, C extends Collection> Request<Collection<T>> patch(String uri, Class<C> collectionType,
                                                                  Class<T> entityType) {
        return this.req(uri).patch(collectionType, entityType);
    }

    @Override
    public <V, K, M extends Map> Request<Map<K, V>> patch(String uri, Class<M> mapType, Class<K> keyType,
                                                          Class<V> valueType) {
        return this.req(uri).patch(mapType, keyType, valueType);
    }

    @Override
    public Request<Headers> head(String uri) {
        return this.req(uri).head();
    }

    @Override
    public Request<Headers> options(String uri) {
        return this.req(uri).options();
    }

    //===================================================================
    // Session configuration
    //===================================================================

    public AsyncRunner getAsyncRunner() {
        return asyncRunner;
    }

    public RequestLogger getLogger() {
        return logger;
    }

    public RequestDispatcher.Factory getRequestDispatcherFactory() {
        return requestDispatcherFactory;
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        requestProcessor.setRequestSerializer(requestSerializer);
    }

    public RequestSerializer getRequestSerializer() {
        return requestProcessor.getRequestSerializer();
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        responseProcessor.setResponseDeserializer(responseDeserializer);
    }

    public ResponseDeserializer getResponseDeserializer() {
        return responseProcessor.getResponseDeserializer();
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
        options.reset();
    }

    @Override
    public void setMediaType(String mediaType) {
        options.setMediaType(mediaType);
    }

    @Override
    public String getMediaType() {
        return options.getMediaType();
    }

    @Override
    public void setAuth(Auth auth) {
        options.setAuth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        options.setAuth(authProvider);
    }

    @Override
    public Auth getAuth() {
        return options.getAuth();
    }

    @Override
    public Auth.Provider getAuthProvider() {
        return options.getAuthProvider();
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        options.setTimeout(timeoutMillis);
    }

    @Override
    public int getTimeout() {
        return options.getTimeout();
    }

    @Override
    public void setDelay(int delayMillis) {
        options.setDelay(delayMillis);
    }

    @Override
    public int getDelay() {
        return options.getDelay();
    }

    @Override
    public void setCharset(String charset) {
        options.setCharset(charset);
    }

    @Override
    public String getCharset() {
        return options.getCharset();
    }

    @Override
    public void setRetry(int[] delaysMillis, RequestEvent... events) {
        options.setRetry(delaysMillis, events);
    }

    @Override
    public void setRetry(RetryPolicy retryPolicy) {
        options.setRetry(retryPolicy);
    }

    @Override
    public void setRetry(RetryPolicy.Provider retryPolicyProvider) {
        options.setRetry(retryPolicyProvider);
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return options.getRetryPolicy();
    }

    @Override
    public RetryPolicy.Provider getRetryPolicyProvider() {
        return options.getRetryPolicyProvider();
    }

    @Override
    public boolean isRetryEnabled() {
        return options.isRetryEnabled();
    }

    @Override
    public void setHeader(Header header) {
        options.setHeader(header);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        options.setHeader(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return options.getHeaders();
    }

    @Override
    public String getHeader(String headerName) {
        return options.getHeader(headerName);
    }

    @Override
    public boolean hasHeader(String headerName) {
        return options.hasHeader(headerName);
    }

    @Override
    public Header delHeader(String headerName) {
        return options.delHeader(headerName);
    }

    /**
     * Register a auto generated {@link SerializationModule}.
     *
     * @param classes The module interfaces containing one or many generated serializers
     *
     * @return The {@link Registration} object, capable of cancelling this registration
     */
    public Registration register(Class<? extends SerializationModule>... classes) {
        final List<Registration> registrations = new LinkedList<Registration>();

        for (Class<? extends SerializationModule> cls : classes) {
            registrations.add(register(Reflection.newImpl(cls)));
        }

        return new Registration() {
            public void cancel() {
                for (Registration registration : registrations) {
                    registration.cancel();
                }
            }
        };
    };

    /**
     * Register a {@link SerializationModule}.
     *
     * @param serializationModule  The module containing one or many serializers
     *
     * @return The {@link Registration} object, capable of cancelling this registration
     */
    public Registration register(SerializationModule serializationModule) {
        final List<Registration> registrations = new LinkedList<Registration>();

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

    //===================================================================
    // Store methods
    //===================================================================

    /**
     * @deprecated use {@link #getValue(String)} instead.
     */
    @Override
    @Deprecated
    public <T> T retrieve(String key) {
        return getValue(key);
    }

    @Override
    public <T> T getValue(String key) {
        return store.getValue(key);
    }

    @Override
    public Data getData(String key) {
        return store.getData(key);
    }

    @Override
    public Session save(String key, Object value) {
        store.save(key, value);
        return this;
    }

    @Override
    public Session save(String key, Object value, Level level) {
        store.save(key, value, level);
        return this;
    }

    @Override
    public Session save(String key, Object value, long ttl, Level level) {
        store.save(key, value, ttl, level);
        return this;
    }

    @Override
    public Session save(String key, Object value, long ttl) {
        store.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return store.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return store.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return store.remove(key);
    }

    @Override
    public Data refresh(String key, long ttlMillis) {
        return store.refresh(key, ttlMillis);
    }

    @Override
    public Data refresh(String key) {
        return store.refresh(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        store.clear(fireRemovedEvent);
    }

    @Override
    public Session onSaved(String key, Handler handler) {
        store.onSaved(key, handler);
        return this;
    }

    @Override
    public Session onRemoved(String key, Handler handler) {
        store.onRemoved(key, handler);
        return this;
    }

    @Override
    public Session onExpired(String key, Handler handler) {
        store.onExpired(key, handler);
        return this;
    }

    public void shutdown() {
        asyncRunner.shutdown();
    }

    public boolean isShutdown() {
        return asyncRunner.isShutdown();
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected RequestOptionsHolder getRequestOptions() {
        return options;
    }

    private RequestInvoker createRequest(Uri uri) {
        final RequestInvoker request = new RequestInvokerImpl(this, uri, new LeafStore(store, false, asyncRunner),
                requestDispatcherFactory.create(asyncRunner, requestProcessor, responseProcessor, deferredPoolFactory,
                        logger));

        options.apply(request);

        return request;
    }
}
