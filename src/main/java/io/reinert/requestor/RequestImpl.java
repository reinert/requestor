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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Default implementation for {@link Request}.
 *
 * @author Danilo Reinert
 */
public class RequestImpl implements RequestInvoker, io.reinert.requestor.Request {

    private final RequestDispatcher server = GWT.create(RequestDispatcher.class);
    private final String url;
    private final FilterEngine filterEngine;
    private final SerializationEngine serializationEngine;

    private String httpMethod;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private String contentType;
    private AcceptHeader accept;
    private Object payload;

    public RequestImpl(String url, SerializationEngine serializationEngine,
                       FilterEngine filterEngine) {
        this.filterEngine = filterEngine;
        this.serializationEngine = serializationEngine;
        this.url = url;
    }

    @Override
    public RequestInvoker contentType(String mediaType) {
        this.contentType = mediaType;
        return this;
    }

    @Override
    public RequestInvoker accept(String mediaType) {
        this.accept = new AcceptHeader(mediaType);
        return this;
    }

    @Override
    public RequestInvoker accept(AcceptHeader acceptHeader) {
        this.accept = acceptHeader;
        return this;
    }

    @Override
    public RequestInvoker header(String header, String value) {
        ensureHeaders().add(new SimpleHeader(header, value));
        return this;
    }

    @Override
    public RequestInvoker header(Header header) {
        ensureHeaders().add(header);
        return this;
    }

    @Override
    public RequestInvoker user(String user) {
        this.user = user;
        return this;
    }

    @Override
    public RequestInvoker password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public RequestInvoker timeout(int timeoutMillis) {
        if (timeoutMillis < 0)
            throw new IllegalArgumentException("Timeout cannot be negative.");
        timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestInvoker payload(Object object) throws IllegalArgumentException {
        payload = object;
        return this;
    }

    @Override
    public RequestPromise<Void> get() {
        return send("GET", Void.class);
    }

    @Override
    public <T> RequestPromise<T> get(Class<T> responseType) {
        return send("GET", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> get(Class<T> responseType, Class<C> containerType) {
        return send("GET", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> post() {
        return send("POST", Void.class);
    }

    @Override
    public <T> RequestPromise<T> post(Class<T> responseType) {
        return send("POST", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> post(Class<T> responseType, Class<C> containerType) {
        return send("POST", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> put() {
        return send("PUT", Void.class);
    }

    @Override
    public <T> RequestPromise<T> put(Class<T> responseType) {
        return send("PUT", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> put(Class<T> responseType, Class<C> containerType) {
        return send("PUT", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> delete() {
        return send("DELETE", Void.class);
    }

    @Override
    public <T> RequestPromise<T> delete(Class<T> responseType) {
        return send("DELETE", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                          Class<C> containerType) {
        return send("DELETE", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> head() {
        return send("HEAD", Void.class);
    }

    @Override
    public <T> RequestPromise<T> head(Class<T> responseType) {
        return send("HEAD", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType) {
        return send("HEAD", responseType, containerType);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public AcceptHeader getAccept() {
        return accept;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    private <T> RequestPromise<T> send(String method, Class<T> responseType) {
        this.httpMethod = method;
        final DeferredSingleResult<T> deferred = new DeferredSingleResult<T>(responseType, serializationEngine);
        ConnectionCallback callback = createConnectionCallback(deferred);
        dispatch(callback);
        return deferred;
    }

    private <T, C extends Collection> RequestPromise<Collection<T>> send(String method,
                                                                         Class<T> responseType,
                                                                         Class<C> containerType) {
        this.httpMethod = method;
        final DeferredCollectionResult<T> deferred = new DeferredCollectionResult<T>(responseType, containerType,
                serializationEngine);
        ConnectionCallback callback = createConnectionCallback(deferred);
        dispatch(callback);
        return deferred;
    }

    private <D> ConnectionCallback createConnectionCallback(final DeferredRequest<D> deferred) {
        return new ConnectionCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    final ResponseImpl responseWrapper = new ResponseImpl(response);

                    // Execute filters on this response
                    filterEngine.applyResponseFilters(RequestImpl.this, responseWrapper);

                    if (response.getStatusCode() / 100 == 2) {
                        deferred.resolve(RequestImpl.this, responseWrapper);
                    } else {
                        deferred.reject(new UnsuccessfulResponseException(RequestImpl.this,
                                responseWrapper));
                    }
                }

                @Override
                public void onProgress(RequestProgress requestProgress) {
                    deferred.notify(requestProgress);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    if (exception instanceof com.google.gwt.http.client.RequestTimeoutException) {
                        com.google.gwt.http.client.RequestTimeoutException e =
                                (com.google.gwt.http.client.RequestTimeoutException) exception;
                        deferred.reject(new io.reinert.requestor.RequestTimeoutException(RequestImpl.this,
                                e.getTimeoutMillis()));
                    } else {
                        deferred.reject(new io.reinert.requestor.RequestException(exception));
                    }
                }
            };
    }

    private Connection dispatch(ConnectionCallback callback) {
        ensureHeaders();

        // Execute filters on this request
        filterEngine.applyRequestFilters(RequestImpl.this);

        return server.send(this, serializationEngine, callback);
    }

    private Headers ensureHeaders() {
        if (headers == null) {
            headers = new Headers();
            headers.add(new ContentTypeHeader(contentType));
            headers.add(accept);
        }
        return headers;
    }
}
