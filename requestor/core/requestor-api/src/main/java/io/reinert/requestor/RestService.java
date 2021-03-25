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

import java.util.Collection;

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

public class RestService<R, I, C extends Collection> implements RestInvoker<R, I>, RequestDefaults {

    protected final Requestor requestor;
    protected final UriBuilder uriBuilder;
    protected final Class<R> resourceType;
    protected final Class<I> idType;
    protected final Class<C> collectionType;
    protected final RequestDefaultsImpl defaults;

    private boolean asMatrixParam = false;

    protected RestService(Requestor requestor, String resourceUri, Class<R> resourceType, Class<I> idType,
                          Class<C> collectionType, RequestDefaultsImpl defaults) {
        this.requestor = requestor;
        this.resourceType = resourceType;
        this.idType = idType;
        this.collectionType = collectionType;
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
        this.defaults = defaults;
    }

    @Override
    public Promise<Collection<R>> get(String... params) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        appendParamsToUri(reqUriBuilder, params);

        return request(reqUriBuilder.build()).get(resourceType, collectionType);
    }

    @Override
    public Promise<R> get(I id, String... params) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        reqUriBuilder.segment(id);

        appendParamsToUri(reqUriBuilder, params);

        return request(reqUriBuilder.build()).get(resourceType);
    }

    @Override
    public Promise<SerializedResponse> post(R resource) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        return request(reqUriBuilder.build()).payload(resource).post(SerializedResponse.class);
    }

    @Override
    public Promise<SerializedResponse> put(I id, R resource) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        reqUriBuilder.segment(id);

        return request(reqUriBuilder.build()).payload(resource).put(SerializedResponse.class);
    }

    @Override
    public Promise<SerializedResponse> delete(I id) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        reqUriBuilder.segment(id);

        return request(reqUriBuilder.build()).delete(SerializedResponse.class);
    }

    @Override
    public RequestInvoker req() {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        return request(reqUriBuilder.build());
    }

    @Override
    public RequestInvoker req(I id) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        reqUriBuilder.segment(id);

        return request(reqUriBuilder.build());
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
    public Auth getAuth() {
        return defaults.getAuth();
    }

    @Override
    public void setTimeout(int timeout) {
        defaults.setTimeout(timeout);
    }

    @Override
    public int getTimeout() {
        return defaults.getTimeout();
    }

    @Override
    public void addHeader(Header header) {
        defaults.addHeader(header);
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        defaults.addHeader(headerName, headerValue);
    }

    @Override
    public Header getHeader(String headerName) {
        return defaults.getHeader(headerName);
    }

    @Override
    public void removeHeader(String headerName) {
        defaults.removeHeader(headerName);
    }

    public void setAsMatrixParam(boolean asMatrixParam) {
        this.asMatrixParam = asMatrixParam;
    }

    public boolean isAsMatrixParam() {
        return this.asMatrixParam;
    }

    protected void appendParamsToUri(UriBuilder reqUriBuilder, String[] params) {
        // Check if params were given to the URI
        if (params != null && params.length > 0) {
            if (params.length % 2 > 0) {
                throw new IllegalArgumentException("It should have an even number of arguments, consisting of " +
                        "key and value pairs which will be appended to the request URI");
            }

            // Append the params to the request URI
            for (int i = 0; i < params.length; i = i + 2) {
                if (asMatrixParam) {
                    reqUriBuilder.matrixParam(params[i], params[i + 1]);
                } else {
                    reqUriBuilder.queryParam(params[i], params[i + 1]);
                }
            }
        }
    }

    private RequestInvoker request(Uri uri) {
        final RequestInvoker request = requestor.req(uri);
        defaults.apply(request);
        return request;
    }
}
