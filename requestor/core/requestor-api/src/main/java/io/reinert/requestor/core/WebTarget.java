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
package io.reinert.requestor.core;

import java.util.HashMap;
import java.util.Map;

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;
import io.reinert.requestor.core.uri.UriBuilderException;

/**
 * A resource target identified by the resource URI.
 * <p></p>
 * Any Filters or Interceptors registered in a WebTarget instance do not affect others derived from the original.
 *
 * @author Danilo Reinert
 */
public class WebTarget implements FilterManager, InterceptorManager, HasRequestOptions {

    private static final String COULD_NOT_BUILD_THE_URI = "Could not build the URI.";

    private final RequestOptionsHolder options;
    private final SerializationEngine serializationEngine;
    private final RequestDispatcher.Factory requestDispatcherFactory;
    private final Deferred.Factory deferredFactory;
    private final FilterManagerImpl filterManager;
    private final InterceptorManagerImpl interceptorManager;
    private final TransientStore store;
    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final RequestDispatcher requestDispatcher;
    private final UriBuilder uriBuilder;
    private Uri uri;

    public static WebTarget create(FilterManagerImpl filterManager, InterceptorManagerImpl interceptorManager,
                                   SerializationEngine serializationEngine, RequestDispatcher.Factory dispatcherFactory,
                                   Deferred.Factory deferredFactory, SessionStore store, RequestOptionsHolder defaults,
                                   Uri uri) {
        return new WebTarget(filterManager, interceptorManager, serializationEngine, dispatcherFactory,
                deferredFactory, new TransientStore(store), RequestOptionsHolder.copy(defaults), uri,
                uri == null ? UriBuilder.newInstance() : UriBuilder.fromUri(uri));
    }

    public static WebTarget create(FilterManagerImpl filterManager, InterceptorManagerImpl interceptorManager,
                                   SerializationEngine serializationEngine, RequestDispatcher.Factory dispatcherFactory,
                                   Deferred.Factory deferredFactory, SessionStore store, RequestOptionsHolder defaults,
                                   UriBuilder uriBuilder) {
        return new WebTarget(filterManager, interceptorManager, serializationEngine, dispatcherFactory,
                deferredFactory, new TransientStore(store), RequestOptionsHolder.copy(defaults),null,
                uriBuilder);
    }

    private WebTarget(FilterManagerImpl filterManager, InterceptorManagerImpl interceptorManager,
                      SerializationEngine serializationEngine, RequestDispatcher.Factory requestDispatcherFactory,
                      Deferred.Factory deferredFactory, TransientStore store,
                      RequestOptionsHolder options, Uri uri, UriBuilder uriBuilder) {
        this.serializationEngine = serializationEngine;
        this.requestDispatcherFactory = requestDispatcherFactory;
        this.deferredFactory = deferredFactory;
        this.filterManager = new FilterManagerImpl(filterManager);
        this.interceptorManager = new InterceptorManagerImpl(interceptorManager);
        this.store = store;
        this.options = options;

        this.requestProcessor = new RequestProcessor(
                serializationEngine,
                options.getRequestSerializer(),
                this.filterManager,
                this.interceptorManager);

        this.responseProcessor = new ResponseProcessor(
                serializationEngine,
                options.getResponseDeserializer(),
                this.filterManager,
                this.interceptorManager);

        this.requestDispatcher = requestDispatcherFactory.newRequestDispatcher(
                this.requestProcessor,
                this.responseProcessor,
                deferredFactory);

        this.uriBuilder = uriBuilder;
        this.uri = uri;
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
    public void setPolling(PollingStrategy strategy, int intervalMillis, int limit) {
        options.setPolling(strategy, intervalMillis, limit);
    }

    @Override
    public boolean isPolling() {
        return options.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return options.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return options.getPollingLimit();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return options.getPollingStrategy();
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
    public Header delHeader(String headerName) {
        return options.delHeader(headerName);
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        options.setRequestSerializer(requestSerializer);
        requestProcessor.setRequestSerializer(requestSerializer);
    }

    public RequestSerializer getRequestSerializer() {
        return options.getRequestSerializer();
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        options.setResponseDeserializer(responseDeserializer);
        responseProcessor.setResponseDeserializer(responseDeserializer);
    }

    public ResponseDeserializer getResponseDeserializer() {
        return options.getResponseDeserializer();
    }

    /**
     * Get the URI identifying the resource.
     *
     * @return  the resource URI.
     *
     * @throws IllegalStateException  if the URI could not be built from the current state of the resource target.
     */
    public Uri getUri() {
        if (uri == null) {
            try {
                uri = uriBuilder.build();
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(COULD_NOT_BUILD_THE_URI, e);
            } catch (UriBuilderException e) {
                throw new IllegalStateException(COULD_NOT_BUILD_THE_URI, e);
            }
        }
        return uri;
    }

//    /**
//     * Get the URI builder initialized with the URI of the current resource target. The returned URI builder is
//     * detached from the target, i.e. any updates in the URI builder MUST NOT have any effects on the URI of the
//     * originating target.
//     *
//     * @return  the initialized URI builder
//     */
//    public UriBuilder getUriBuilder() {
//        return uriBuilder;
//    }

    /**
     * Create a new WebTarget instance by appending a matrix parameter to the existing set of matrix parameters of the
     * current final segment of the URI of the current target instance. If multiple values are supplied the parameter
     * will be added once per value.
     * <p></p>
     * Note that the matrix parameters are tied to a particular path segment; appending a value to an existing matrix
     * parameter name will not affect the position of the matrix parameter in the URI path.
     * <p></p>
     * A snapshot of the present configuration of the current (parent) target instance is taken and is inherited by the
     * newly constructed (child) target instance.
     *
     * @param name    the matrix parameter name, may contain URI template parameters.
     * @param values  the query parameter value(s), each object will be converted to a {@code String} using its
     *                {@code toString()} method. Stringified values may contain URI template parameters.
     *
     * @return  a new target instance.
     */
    public WebTarget matrixParam(String name, Object... values) {
        final UriBuilder copy = cloneUriBuilder();
        copy.matrixParam(name, values);
        return newWebTarget(copy);
    }

    /**
     * Create a new WebTarget instance by appending path to the URI of the current target instance.
     * <p></p>
     * When constructing the final path, a '/' separator will be inserted between the existing path and the supplied
     * path if necessary. Existing '/' characters are preserved thus a single value can represent multiple URI path
     * segments.
     * <p></p>
     * A snapshot of the present configuration of the current (parent) target instance is taken and is inherited by the
     * newly constructed (child) target instance.
     *
     * @param path  the path, may contain URI template parameters.
     *
     * @return  a new target instance.
     *
     * @throws IllegalArgumentException  if path is null.
     */
    public WebTarget path(String path) {
        if (path == null) throw new IllegalArgumentException("The path argument of WebTarget cannot be null.");
        final UriBuilder copy = cloneUriBuilder();
        // FIXME: must extract current path and append to the given, because UriBuilder#path overwrites existing path
        copy.path(path);
        return newWebTarget(copy);
    }

    /**
     * Create a new WebTarget instance by configuring a query parameter on the URI of the current target instance.
     * If multiple values are supplied the parameter will be added once per value.
     * <p></p>
     * A snapshot of the present configuration of the current (parent) target instance is taken and is inherited by the
     * newly constructed (child) target instance.
     *
     * @param name    the query parameter name, may contain URI template parameters.
     * @param values  the query parameter value(s), each object will be converted to a {@code String} using its
     *                {@code toString()} method. Stringified values may contain URI template parameters.
     *
     * @return  a new target instance.
     */
    public WebTarget queryParam(String name, Object... values) {
        final UriBuilder copy = cloneUriBuilder();
        copy.queryParam(name, values);
        return newWebTarget(copy);
    }

    /**
     * Start building a request to the targeted web resource.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     *
     * @throws IllegalStateException  if the URI could not be built from the current state of the resource target.
     */
    public RequestInvoker resolve() {
        return createRequest(getUri());
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param templateValues  a map of URI template names and their values.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(Map<String, ?> templateValues) {
        final Uri resolvedUri;
        try {
            resolvedUri = uriBuilder.build(templateValues);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not build the URI with the supplied template values.", e);
        }
        return createRequest(resolvedUri);
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param name   name of the URI template.
     * @param value  value to be used to resolve the template.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(String name, Object value) {
        final HashMap<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(name, value);
        return resolve(templateValues);
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param n1  name of one URI template.
     * @param v1  value to be used to resolve the template {@code n1}.
     * @param n2  name of one URI template.
     * @param v2  value to be used to resolve the template {@code n2}.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(String n1, Object v1, String n2, Object v2) {
        final HashMap<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(n1, v1);
        templateValues.put(n2, v2);
        return resolve(templateValues);
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param n1  name of one URI template.
     * @param v1  value to be used to resolve the template {@code n1}.
     * @param n2  name of one URI template.
     * @param v2  value to be used to resolve the template {@code n2}.
     * @param n3  name of one URI template.
     * @param v3  value to be used to resolve the template {@code n3}.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(String n1, Object v1, String n2, Object v2, String n3, Object v3) {
        final HashMap<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(n1, v1);
        templateValues.put(n2, v2);
        templateValues.put(n3, v3);
        return resolve(templateValues);
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param n1  name of one URI template.
     * @param v1  value to be used to resolve the template {@code n1}.
     * @param n2  name of one URI template.
     * @param v2  value to be used to resolve the template {@code n2}.
     * @param n3  name of one URI template.
     * @param v3  value to be used to resolve the template {@code n3}.
     * @param n4  name of one URI template.
     * @param v4  value to be used to resolve the template {@code n4}.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(String n1, Object v1, String n2, Object v2, String n3, Object v3,
                                  String n4, Object v4) {
        final HashMap<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(n1, v1);
        templateValues.put(n2, v2);
        templateValues.put(n3, v3);
        templateValues.put(n4, v4);
        return resolve(templateValues);
    }

    /**
     * Resolves the URI template parameters of the current target instance using supplied name-value pairs and start
     * building a request to the targeted web resource.
     *
     * @param n1  name of one URI template.
     * @param v1  value to be used to resolve the template {@code n1}.
     * @param n2  name of one URI template.
     * @param v2  value to be used to resolve the template {@code n2}.
     * @param n3  name of one URI template.
     * @param v3  value to be used to resolve the template {@code n3}.
     * @param n4  name of one URI template.
     * @param v4  value to be used to resolve the template {@code n4}.
     * @param n5  name of one URI template.
     * @param v5  value to be used to resolve the template {@code n5}.
     *
     * @return  builder for a request targeted at the URI referenced by the resolved target instance.
     */
    public RequestInvoker resolve(String n1, Object v1, String n2, Object v2, String n3, Object v3,
                                  String n4, Object v4, String n5, Object v5) {
        final HashMap<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put(n1, v1);
        templateValues.put(n2, v2);
        templateValues.put(n3, v3);
        templateValues.put(n4, v4);
        templateValues.put(n5, v5);
        return resolve(templateValues);
    }

    private WebTarget newWebTarget(UriBuilder copy) {
        return new WebTarget(filterManager, interceptorManager, serializationEngine, requestDispatcherFactory,
                deferredFactory, TransientStore.copy(store), RequestOptionsHolder.copy(options),null, copy);
    }

    private UriBuilder cloneUriBuilder() {
        return uriBuilder.clone();
    }

    private RequestInvoker createRequest(Uri uri) {
        final RequestInvokerImpl request =
                new RequestInvokerImpl(uri, new TransientStore(store), requestDispatcher);
        options.apply(request);
        return request;
    }
}