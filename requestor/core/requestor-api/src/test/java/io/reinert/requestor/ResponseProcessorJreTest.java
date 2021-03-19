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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link ResponseProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseProcessorJreTest {

    @Mock private FilterEngine filterEngine;
    @Mock private InterceptorEngine interceptorEngine;
    @Mock private SerializationEngine serializationEngine;
    private ResponseProcessor processor;

    @Before
    public void setUp() {
        processor = new ResponseProcessor(serializationEngine, filterEngine, interceptorEngine);
    }

    @Test
    public void processWithSingleType_SuccessfulResponse_ShouldFilterInterceptSerializeAndResolve() {
        // Given
        final Class<Object> resolveType = Object.class;
        final Deferred<Object> deferred = mockDeferred();

        final RawResponse response = mockRawResponse();
        final Request request = mockRequest();

        final Response<Object> deserializedResponse = mockResponse();

        setupRawResponse(response, Response.Status.OK);
        setupSerializationEngine(resolveType, response, request, deserializedResponse);

        // When
        processor.process(request, response, resolveType, deferred);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine, deferred);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(serializationEngine).deserializeResponse(eq(request), eq(response), eq(resolveType));
        inOrder.verify(deferred).resolve(eq(deserializedResponse));
    }

    @Test
    public void processWithParametrizedType_SuccessfulResponse_ShouldFilterInterceptSerializeAndResolve() {
        // Given
        final Class<Object> parametrizedType = Object.class;
        final Class<Collection> containerType = Collection.class;
        final Deferred<Collection> deferred = mockDeferred();

        final RawResponse response = mockRawResponse();
        final Request request = mockRequest();

        final Response<Collection> deserializedResponse = mockResponse();

        setupRawResponse(response, Response.Status.OK);
        setupSerializationEngine(parametrizedType, containerType, response, request, deserializedResponse);

        // When
        processor.process(request, response, parametrizedType, containerType, deferred);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine, deferred);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(serializationEngine).deserializeResponse(eq(request), eq(response), eq(parametrizedType),
                eq(containerType));
        inOrder.verify(deferred).resolve(eq(deserializedResponse));
    }

    @Test
    public void processWithSingleType_UnsuccessfulResponse_ShouldFilterInterceptAndReject() {
        // Given
        final Class<Object> resolveType = Object.class;
        final Deferred<Object> deferred = mockDeferred();

        final RawResponse response = mockRawResponse();
        final Request request = mockRequest();

        final Response<Object> deserializedResponse = mockResponse();

        setupRawResponse(response, Response.Status.BAD_REQUEST);
        setupSerializationEngine(resolveType, response, request, deserializedResponse);

        // When
        processor.process(request, response, resolveType, deferred);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine, deferred);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(deferred).reject(any(RequestException.class));
    }

    @Test
    public void processWithParametrizedType_UnuccessfulResponse_ShouldFilterInterceptAndReject() {
        // Given
        final Class<Object> parametrizedType = Object.class;
        final Class<Collection> containerType = Collection.class;
        final Deferred<Collection> deferred = mockDeferred();

        final RawResponse response = mockRawResponse();
        final Request request = mockRequest();

        final Response<Collection> deserializedResponse = mockResponse();

        setupRawResponse(response, Response.Status.INTERNAL_SERVER_ERROR);
        setupSerializationEngine(parametrizedType, containerType, response, request, deserializedResponse);

        // When
        processor.process(request, response, parametrizedType, containerType, deferred);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine, deferred);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(deferred).reject(any(RequestException.class));
    }

    @SuppressWarnings("unchecked")
    private <T> Deferred<T> mockDeferred() {
        return mock(Deferred.class);
    }

    // private Headers mockHeaders() {
    //     return mock(Headers.class);
    // }

    private RawResponse mockRawResponse() {
        return mock(RawResponse.class);
    }

    private Request mockRequest() {
        return mock(Request.class);
    }

    @SuppressWarnings("unchecked")
    private <T> Response<T> mockResponse() {
        return mock(Response.class);
    }

    private void setupRawResponse(RawResponse response, Response.Status responseStatus) {
        when(response.getResponseType()).thenReturn(ResponseType.DEFAULT);
        // This stub is unnecessary according to mockito
        // when(response.getHeaders()).thenReturn(mockHeaders());
        when(response.getStatusCode()).thenReturn(responseStatus.getStatusCode());
    }

    private <T> void setupSerializationEngine(Class<T> resolveType, RawResponse response, Request request,
                                              Response<T> deserializedResponse) {
        when(serializationEngine.deserializeResponse(request, response, resolveType))
                .thenReturn(deserializedResponse);
    }

    private <C extends Collection> void setupSerializationEngine(Class<?> parametrizedType, Class<C> containerType,
                                                                 RawResponse response, Request request,
                                                                 Response<C> deserializedResponse) {
        when(serializationEngine.deserializeResponse(request, response, parametrizedType, containerType))
                .thenReturn(deserializedResponse);
    }
}
