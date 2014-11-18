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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestCallbackWithProgress;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestProgress;
import com.google.gwt.http.client.Response;

import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.SimpleHeader;
import io.reinert.requestor.serialization.SerdesManager;
import io.reinert.requestor.serialization.Serializer;

/**
 * Default implementation for {@link Request}.
 */
public class RequestImpl implements RequestDispatcher, io.reinert.requestor.Request {

    private final Server server = GWT.create(Server.class);
    private final SerdesManager serdesManager;
    private final ProviderManager providerManager;
    private final String url;
    private final FilterManager filterManager;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private String contentType;
    private AcceptHeader accept;
    private Object payload;

    public RequestImpl(String url, SerdesManager serdesManager, ProviderManager providerManager,
                       FilterManager filterManager) {
        this.serdesManager = serdesManager;
        this.providerManager = providerManager;
        this.filterManager = filterManager;
        // TODO: parse URI
        this.url = url;
    }

    @Override
    public RequestDispatcher contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public RequestDispatcher accept(String contentType) {
        this.accept = new AcceptHeader(contentType);
        return this;
    }

    @Override
    public RequestDispatcher accept(AcceptHeader acceptHeader) {
        this.accept = acceptHeader;
        return this;
    }

    @Override
    public RequestDispatcher header(String header, String value) {
        ensureHeaders().add(new SimpleHeader(header, value));
        return this;
    }

    @Override
    public RequestDispatcher header(Header header) {
        ensureHeaders().add(header);
        return this;
    }

    @Override
    public RequestDispatcher user(String user) {
        this.user = user;
        return this;
    }

    @Override
    public RequestDispatcher password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public RequestDispatcher timeout(int timeoutMillis) {
        if (timeoutMillis < 0)
            throw new IllegalArgumentException("Timeout cannot be negative.");
        timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestDispatcher payload(Object object) throws IllegalArgumentException {
        payload = object;
        return this;
    }

    @Override
    public RequestPromise<Void> get() {
        return send(RequestBuilder.GET, Void.class);
    }

    @Override
    public <T> RequestPromise<T> get(Class<T> responseType) {
        return send(RequestBuilder.GET, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> get(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.GET, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> post() {
        return send(RequestBuilder.POST, Void.class);
    }

    @Override
    public <T> RequestPromise<T> post(Class<T> responseType) {
        return send(RequestBuilder.POST, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> post(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.POST, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> put() {
        return send(RequestBuilder.PUT, Void.class);
    }

    @Override
    public <T> RequestPromise<T> put(Class<T> responseType) {
        return send(RequestBuilder.PUT, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> put(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.PUT, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> delete() {
        return send(RequestBuilder.DELETE, Void.class);
    }

    @Override
    public <T> RequestPromise<T> delete(Class<T> responseType) {
        return send(RequestBuilder.DELETE, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                          Class<C> containerType) {
        return send(RequestBuilder.DELETE, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> head() {
        return send(RequestBuilder.HEAD, Void.class);
    }

    @Override
    public <T> RequestPromise<T> head(Class<T> responseType) {
        return send(RequestBuilder.HEAD, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.HEAD, responseType, containerType);
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

    private <T> RequestPromise<T> send(RequestBuilder.Method method, Class<T> responseType) {
        final DeferredSingleResult<T> deferred = new DeferredSingleResult<T>(responseType, serdesManager,
                providerManager);

        RequestCallback callback = createRequestCallback(deferred);

        dispatch(method, callback);

        return deferred;
    }

    private <T, C extends Collection> RequestPromise<Collection<T>> send(RequestBuilder.Method method,
                                                                         Class<T> responseType,
                                                                         Class<C> containerType) {
        final DeferredCollectionResult<T> deferred = new DeferredCollectionResult<T>(responseType, containerType,
                serdesManager, providerManager);

        RequestCallback callback = createRequestCallback(deferred);

        dispatch(method, callback);

        return deferred;
    }

    private <D> RequestCallback createRequestCallback(final DeferredRequest<D> deferred) {
        return new RequestCallbackWithProgress() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // Execute filters on this response
                    final List<ResponseFilter> filters = filterManager.getResponseFilters();
                    for (ResponseFilter filter : filters) {
                        filter.filter(RequestImpl.this, response);
                    }

                    if (response.getStatusCode() / 100 == 2) {
                        deferred.resolve(RequestImpl.this, new ResponseImpl(response));
                    } else {
                        deferred.reject(new UnsuccessfulResponseException(RequestImpl.this,
                                new ResponseImpl(response)));
                    }
                }

                @Override
                public void onProgress(RequestProgress requestProgress) {
                    deferred.notify(new RequestProgressImpl(requestProgress));
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

    private void dispatch(RequestBuilder.Method method, RequestCallback callback) {
        ensureHeaders();

        // Execute filters on this request
        final List<RequestFilter> filters = filterManager.getRequestFilters();
        for (RequestFilter filter : filters) {
            filter.filter(this);
        }

        String body = serializePayload();

        ServerConnection connection = server.getConnection();

        try {
            connection.sendRequest(timeout, user, password, headers, method, url, body, callback);
        } catch (final RequestException e) {
            throw new RequestDispatchException("It was not possible to dispatch the request.", e);
        }
    }

    private String serializePayload() {
        String body = null;

        if (payload != null) {
            if (payload instanceof Collection) {
                Collection c = (Collection) payload;
                final Iterator iterator = c.iterator();
                Object item = null;
                while (iterator.hasNext() && item == null) {
                    item = iterator.next();
                }
                if (item == null) {
                    /* FIXME: This is forcing empty collections responses to be a empty json array.
                        It will cause error for serializers expecting other content-type (e.g. XML).
                       TODO: Create some EmptyCollectionSerializerManager for serialization of empty collections
                        by content-type. */
                    body = "[]";
                } else {
                    Serializer<?> serializer = serdesManager.getSerializer(item.getClass(), contentType);
                    body = serializer.serialize(c, new HttpSerializationContext(url, ensureHeaders()));
                }
            } else {
                @SuppressWarnings("unchecked")
                Serializer<Object> serializer = (Serializer<Object>) serdesManager.getSerializer(payload.getClass(),
                        contentType);
                body = serializer.serialize(payload, new HttpSerializationContext(url, ensureHeaders()));
            }
        }
        return body;
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
