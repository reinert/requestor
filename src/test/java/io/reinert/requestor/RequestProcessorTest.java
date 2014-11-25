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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link RequestProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestProcessorTest {

    @Mock private SerializationEngine serializationEngine;
    @Mock private FilterEngine filterEngine;

    private RequestProcessor processor;

    @Before
    public void setUp() {
        processor = new RequestProcessor(serializationEngine, filterEngine);
    }

    @Test
    public void process_ShouldApplyFiltersThenSerialize() {
        // Given
        RequestBuilder request = mock(RequestBuilder.class);
        when(filterEngine.filterRequest(request)).thenReturn(request);

        // When
        processor.process(request);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine);
        inOrder.verify(filterEngine).filterRequest(request);
        inOrder.verify(serializationEngine).serializeRequest(request);
    }
}
