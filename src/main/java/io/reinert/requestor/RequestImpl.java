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

import com.google.gwt.http.client.Request;

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

    private final RequestDispatcher dispatcher;
    private final String url;
    private String httpMethod;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private String contentType;
    private AcceptHeader accept;
    private Object payload;

    public RequestImpl(RequestDispatcher dispatcher, String url) {
        this.dispatcher = dispatcher;
        this.url = url;
    }

    //===================================================================
    // Request methods
    //===================================================================

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

    //===================================================================
    // RequestBuilder methods
    //===================================================================

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
        if (timeoutMillis > 0)
            timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestInvoker payload(Object object) throws IllegalArgumentException {
        payload = object;
        return this;
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

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
    public RequestPromise<Void> patch() {
        return send("PATCH", Void.class);
    }

    @Override
    public <T> RequestPromise<T> patch(Class<T> responseType) {
        return send("PATCH", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> patch(Class<T> responseType,
                                                                         Class<C> containerType) {
        return send("PATCH", responseType, containerType);
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
    public RequestPromise<Void> options() {
        return send("OPTIONS", Void.class);
    }

    @Override
    public <T> RequestPromise<T> options(Class<T> responseType) {
        return send("OPTIONS", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> options(Class<T> responseType,
                                                                           Class<C> containerType) {
        return send("OPTIONS", responseType, containerType);
    }

    private <T> RequestPromise<T> send(String method, Class<T> responseType) {
        this.httpMethod = method;
        return dispatcher.send(this, responseType);
    }

    private <T, C extends Collection> RequestPromise<Collection<T>> send(String method, Class<T> responseType,
                                                                         Class<C> containerType) {
        this.httpMethod = method;
        return dispatcher.send(this, responseType, containerType);
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
