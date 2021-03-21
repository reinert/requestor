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

import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

public class ResourceService<R, I, C extends Collection> implements ResourceInvoker<R, I> {

    protected final Requestor requestor;
    protected final UriBuilder uriBuilder;
    protected final Class<R> resourceType;
    protected final Class<I> idType;
    protected final Class<C> collectionType;
    private boolean asMatrixParam = false;
    private String mediaType;

    protected ResourceService(Requestor requestor, String resourceUri, Class<R> resourceType, Class<I> idType,
                              Class<C> collectionType) {
        this.requestor = requestor;
        this.resourceType = resourceType;
        this.idType = idType;
        this.collectionType = collectionType;
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
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

    public void setAsMatrixParam(boolean asMatrixParam) {
        this.asMatrixParam = asMatrixParam;
    }

    public boolean isAsMatrixParam() {
        return this.asMatrixParam;
    }

    /**
     * Get default media type of this service.
     *
     * @return  media type
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Set default media type to be added as Content-Type header in every request.
     *
     * @param mediaType HTTP media type
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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
        if (mediaType != null)
            return requestor.req(uri).contentType(mediaType);
        return requestor.req(uri);
    }
}
