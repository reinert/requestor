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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests of {@link ResponseProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseProcessorTest {

    @Mock private SerializationEngine serializationEngine;
    @Mock private FilterEngine filterEngine;
    @Mock private InterceptorEngine interceptorEngine;

    private ResponseProcessor processor;

    @Before
    public void setUp() {
        processor = new ResponseProcessor(serializationEngine, filterEngine, interceptorEngine);
    }

    @Test
    public <R extends SerializedResponse & ResponseFilterContext & ResponseInterceptorContext>
            void process_OneClass_ShouldApplyFiltersThenSerialize() {
        // Given
        final Class<Object> clazz = Object.class;
        @SuppressWarnings("unchecked")
        R response = (R) mock(SerializedResponseImpl.class, withSettings().extraInterfaces(ResponseFilterContext.class,
                ResponseInterceptorContext.class));
        Request request = mock(Request.class);
        when(response.getResponseType()).thenReturn(ResponseType.DEFAULT);
        when(response.getHeaders()).thenReturn(mock(Headers.class));

        // When
        processor.process(request, response, clazz);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(serializationEngine).deserializeResponse(request, response, clazz);
    }

    @Test
    public <R extends SerializedResponse & ResponseFilterContext & ResponseInterceptorContext>
            void process_TwoClasses_ShouldApplyFiltersThenSerialize() {
        // Given
        final Class<Collection> collectionClazz = Collection.class;
        final Class<Object> clazz = Object.class;
        @SuppressWarnings("unchecked")
        R response = (R) mock(SerializedResponseImpl.class, withSettings().extraInterfaces(ResponseFilterContext.class,
                ResponseInterceptorContext.class));
        Request request = mock(Request.class);
        when(response.getResponseType()).thenReturn(ResponseType.DEFAULT);
        when(response.getHeaders()).thenReturn(mock(Headers.class));

        // When
        processor.process(request, response, clazz, collectionClazz);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine);
        inOrder.verify(filterEngine).filterResponse(eq(request), eq(response));
        inOrder.verify(interceptorEngine).interceptResponse(eq(request), eq(response));
        inOrder.verify(serializationEngine).deserializeResponse(request, response, clazz, collectionClazz);
    }
}
