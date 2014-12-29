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

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests of {@link RequestProcessor}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class RequestProcessorTest {

    @Mock private SerializationEngine serializationEngine;
    @Mock private FilterEngine filterEngine;
    @Mock private InterceptorEngine interceptorEngine;

    private RequestProcessor processor;

    @Before
    public void setUp() {
        processor = new RequestProcessor(serializationEngine, filterEngine, interceptorEngine);
    }

    @Test
    public <R extends RequestBuilder & RequestFilterContext> void process_ShouldFilterThenSerializeThenIntercept() {
        // Given
        @SuppressWarnings("unchecked")
        R request = (R) mock(RequestBuilder.class, withSettings().extraInterfaces(RequestFilterContext.class));
        SerializedRequestDelegate interceptorContext = mock(SerializedRequestDelegate.class);
        when(serializationEngine.serializeRequest(request)).thenReturn(interceptorContext);

        // When
        processor.process(request);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine, interceptorEngine);
        inOrder.verify(filterEngine).filterRequest(request);
        inOrder.verify(serializationEngine).serializeRequest(request);
        inOrder.verify(interceptorEngine).interceptRequest(interceptorContext);
    }
}
