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

import java.util.Collection;
import java.util.List;

import io.reinert.requestor.core.uri.UriBuilder;

/**
 * A basic implementation of {@link AbstractService} with most common operations against a REST resource.
 *
 * @author Danilo Reinert
 */
public class RestService<R, I> extends AbstractService implements RestInvoker<R, I> {

    public static class RestServiceSpec<R, I, C extends Collection> {
        private final Class<R> resourceType;
        private final Class<I> idType;
        private final Class<C> collectionType;

        private RestServiceSpec(Class<R> resourceType, Class<I> idType, Class<C> collectionType) {
            this.resourceType = resourceType;
            this.idType = idType;
            this.collectionType = collectionType;
        }

        public RestServiceBuilder<R, I, C> at(String rootPath) {
            return new RestServiceBuilder<R, I, C>(this, rootPath);
        }
    }

    public static class RestServiceBuilder<R, I, C extends Collection> {
        private final RestServiceSpec<R, I, C> spec;
        private final String rootPath;

        private RestServiceBuilder(RestServiceSpec<R, I, C> spec, String rootPath) {
            this.spec = spec;
            this.rootPath = rootPath;
        }

        public RestService<R, I> on(Session session) {
            return new RestService<R, I>(session, rootPath, spec.resourceType, spec.idType, spec.collectionType);
        }
    }

    public static <R, I, C extends Collection> RestServiceSpec<R, I, C> of(Class<R> resourceType, Class<I> idType,
                                                                           Class<C> collectionType) {
        return new RestServiceSpec<R, I, C>(resourceType, idType, collectionType);
    }

    public static <R, I> RestServiceSpec<R, I, List> of(Class<R> resourceType, Class<I> idType) {
        return new RestServiceSpec<R, I, List>(resourceType, idType, List.class);
    }

    protected final Class<R> resourceType;
    protected final Class<I> idType;
    protected final Class<? extends Collection> collectionType;

    private boolean asMatrixParam = false;

    protected RestService(Session session, String resourceUri, Class<R> resourceType, Class<I> idType,
                          Class<? extends Collection> collectionType) {
        super(session, resourceUri);
        this.resourceType = resourceType;
        this.idType = idType;
        this.collectionType = collectionType;
    }

    @Override
    public Request<R> get(I id) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        reqUriBuilder.segment(id);

        return req(reqUriBuilder.build()).get(resourceType);
    }

    @Override
    public Request<Collection<R>> get(Object... params) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        if (asMatrixParam) {
            appendMatrixParamsToUri(reqUriBuilder, params);
        } else {
            appendQueryParamsToUri(reqUriBuilder, params);
        }

        return req(reqUriBuilder.build()).get(collectionType, resourceType);
    }

    @Override
    public Request<R> post(R resource) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        return req(reqUriBuilder.build()).payload(resource).post(resourceType);
    }

    @Override
    public Request<R> patch(I id, R resource, String... fields) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        reqUriBuilder.segment(id);

        return req(reqUriBuilder.build()).payload(resource, fields).patch(resourceType);
    }

    @Override
    public Request<R> put(I id, R resource) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        reqUriBuilder.segment(id);

        return req(reqUriBuilder.build()).payload(resource).put(resourceType);
    }

    @Override
    public Request<Void> delete(I id) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        reqUriBuilder.segment(id);

        return req(reqUriBuilder.build()).delete(Void.class);
    }

    @Override
    public RequestInvoker req() {
        final UriBuilder reqUriBuilder = getUriBuilder();

        return req(reqUriBuilder.build());
    }

    @Override
    public RequestInvoker req(I id) {
        final UriBuilder reqUriBuilder = getUriBuilder();

        reqUriBuilder.segment(id);

        return req(reqUriBuilder.build());
    }

    public void setAsMatrixParam(boolean asMatrixParam) {
        this.asMatrixParam = asMatrixParam;
    }

    public boolean isAsMatrixParam() {
        return this.asMatrixParam;
    }
}
