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

import java.util.Arrays;
import java.util.List;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link SerializationEngine}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SerializationEngineTest {

    @Mock private SerdesManagerImpl serdesManager;
    @Mock private ProviderManagerImpl providerManager;

    private SerializationEngine engine;

    @Before
    public void setUp() {
        engine = new SerializationEngine(serdesManager, providerManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeResponse_OneClass_ShouldDeserializeAsSingleInstance() {
        // Given
        final Class<Object> clazz = Object.class;
        final String mediaType = "don't/matter";
        final Payload payload = Payload.fromText("any serialized payload");

        SerializedResponse response = mock(SerializedResponse.class);
        when(response.getContentType()).thenReturn(mediaType);
        when(response.getPayload()).thenReturn(payload);
        when(response.getHeaders()).thenReturn(mock(Headers.class));

        Request request = mock(Request.class);
        Deserializer deserializer = mock(Deserializer.class);

        when(serdesManager.getDeserializer(clazz, mediaType)).thenReturn(deserializer);

        // When
        engine.deserializeResponse(request, response, clazz);

        // Then
        verify(serdesManager).getDeserializer(clazz, mediaType);
        verify(deserializer, never()).deserialize(any(Class.class), anyString(), any(DeserializationContext.class));
        verify(deserializer).deserialize(eq(payload.isString()), any(DeserializationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeResponse_TwoClasses_ShouldDeserializeAsCollection() {
        // Given
        final Class<List> collectionClazz = List.class;
        final Class<Object> clazz = Object.class;
        final String mediaType = "don't/matter";
        final Payload payload = Payload.fromText("any serialized payload");

        SerializedResponse response = mock(SerializedResponse.class);
        when(response.getContentType()).thenReturn(mediaType);
        when(response.getPayload()).thenReturn(payload);
        when(response.getHeaders()).thenReturn(mock(Headers.class));

        Request request = mock(Request.class);
        Deserializer deserializer = mock(Deserializer.class);

        when(serdesManager.getDeserializer(clazz, mediaType)).thenReturn(deserializer);

        // When
        engine.deserializeResponse(request, response, clazz, collectionClazz);

        // Then
        verify(serdesManager).getDeserializer(clazz, mediaType);
        verify(deserializer, never()).deserialize(anyString(), any(DeserializationContext.class));
        verify(deserializer).deserialize(eq(collectionClazz), eq(payload.isString()),
                any(DeserializationContext.class));
    }

    @Test
    public void serializeRequest_RequestWithoutPayload_ShouldNeverAskSerializationEngine() {
        // Given
        MutableRequest request = mock(MutableRequest.class);
        when(request.getPayload()).thenReturn(null);

        // When
        engine.serializeRequest(request);

        // Then
        verify(serdesManager, never()).getSerializer(any(Class.class), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeRequest_RequestWithSinglePayload_ShouldCallSerializeSingleInstance() {
        // Given
        final Object singleInstance = new Object();
        final String mediaType = "don't/matter";

        MutableRequest request = mock(MutableRequest.class);
        when(request.getPayload()).thenReturn(singleInstance);
        when(request.getContentType()).thenReturn(mediaType);

        Serializer serializer = mock(Serializer.class);
        when(serdesManager.getSerializer(singleInstance.getClass(), mediaType)).thenReturn(serializer);

        // When
        engine.serializeRequest(request);

        // Then
        verify(serdesManager).getSerializer(singleInstance.getClass(), mediaType);
        verify(serializer, never()).serialize(anyCollection(), any(SerializationContext.class));
        verify(serializer).serialize(eq(singleInstance), any(SerializationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeRequest_RequestWithCollectionPayload_ShouldCallSerializeCollectionInstance() {
        // Given
        final List<Object> collectionInstance = Arrays.asList(new Object(), new Object());
        final String mediaType = "don't/matter";

        MutableRequest request = mock(MutableRequest.class);
        when(request.getPayload()).thenReturn(collectionInstance);
        when(request.getContentType()).thenReturn(mediaType);

        Serializer serializer = mock(Serializer.class);
        when(serdesManager.getSerializer(collectionInstance.get(0).getClass(), mediaType)).thenReturn(serializer);

        // When
        engine.serializeRequest(request);

        // Then
        verify(serdesManager).getSerializer(collectionInstance.get(0).getClass(), mediaType);
        verify(serializer, never()).serialize(anyObject(), any(SerializationContext.class));
        verify(serializer).serialize(eq(collectionInstance), any(SerializationContext.class));
    }
}
