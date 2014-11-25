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

/**
 * Unit tests of {@link ResponseProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseProcessorTest {

    @Mock private SerializationEngine serializationEngine;
    @Mock private FilterEngine filterEngine;

    private ResponseProcessor processor;

    @Before
    public void setUp() {
        processor = new ResponseProcessor(serializationEngine, filterEngine);
    }

    @Test
    public void process_OneClass_ShouldApplyFiltersThenSerialize() {
        // Given
        final Class<Object> clazz = Object.class;
        Request request = mock(Request.class);
        SerializedResponse response = mock(SerializedResponse.class);

        // When
        processor.process(request, response, clazz);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine);
        inOrder.verify(serializationEngine).deserializeResponse(request, response, clazz);
        inOrder.verify(filterEngine).filterResponse(eq(request), any(DeserializedResponse.class));
    }

    @Test
    public void process_TwoClasses_ShouldApplyFiltersThenSerialize() {
        // Given
        final Class<Collection> collectionClazz = Collection.class;
        final Class<Object> clazz = Object.class;
        Request request = mock(Request.class);
        SerializedResponse response = mock(SerializedResponse.class);

        // When
        processor.process(request, response, clazz, collectionClazz);

        // Then
        InOrder inOrder = inOrder(serializationEngine, filterEngine);
        inOrder.verify(serializationEngine).deserializeResponse(request, response, clazz, collectionClazz);
        inOrder.verify(filterEngine).filterResponse(eq(request), any(DeserializedResponse.class));
    }
}
