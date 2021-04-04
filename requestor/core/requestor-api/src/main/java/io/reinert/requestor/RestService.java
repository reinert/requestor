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

import io.reinert.requestor.uri.UriBuilder;

public class RestService<R, I, C extends Collection> extends AbstractService implements RestInvoker<R, I> {

    protected final Class<R> resourceType;
    protected final Class<I> idType;
    protected final Class<C> collectionType;
    protected final UriBuilder uriBuilder;

    private boolean asMatrixParam = false;

    protected RestService(Requestor requestor, String resourceUri, Class<R> resourceType, Class<I> idType,
                          Class<C> collectionType) {
        super(requestor);
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
        this.resourceType = resourceType;
        this.idType = idType;
        this.collectionType = collectionType;
    }

    @Override
    public Promise<Collection<R>> get(Object... params) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        if (asMatrixParam) {
            appendMatrixParamsToUri(reqUriBuilder, params);
        } else {
            appendQueryParamsToUri(reqUriBuilder, params);
        }

        return request(reqUriBuilder.build()).get(resourceType, collectionType);
    }

    @Override
    public Promise<R> get(I id) {
        final UriBuilder reqUriBuilder = uriBuilder.clone();

        reqUriBuilder.segment(id);

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
}
