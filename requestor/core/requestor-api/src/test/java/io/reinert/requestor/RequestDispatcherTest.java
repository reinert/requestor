/*
 * Copyright 2015 Danilo Reinert
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

import javax.annotation.Nullable;

import io.reinert.requestor.deferred.Deferred;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link RequestDispatcher}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestDispatcherTest {

    @Mock private ResponseProcessor processor;
    @Mock private DeferredFactory deferredFactory;
    private RequestDispatcher dispatcher;

    @Before
    public void setUp() {
        dispatcher = spy(new RequestDispatcherDummy(processor, deferredFactory));
    }

    @Test
    public void dispatch_OneClass_ShouldCallSend() {
        // Given
        Class<Object> type = Object.class;
        RequestOrder request = mock(RequestOrder.class);
        Deferred deferred = mock(Deferred.class);
        when(request.getAuth()).thenReturn(PassThroughAuth.getInstance());
        when(deferredFactory.getDeferred()).thenReturn(deferred);

        // When
        dispatcher.dispatch(request, type);

        // Then
        verify(dispatcher).send(any(RequestOrder.class), eq(deferred), eq(type), isNull(Class.class));
    }

    @Test
    public void dispatch_TwoClasses_ShouldCallSend() {
        // Given
        Class<Collection> collectionType = Collection.class;
        Class<Object> type = Object.class;
        SerializedRequest request = mock(SerializedRequest.class);
        Deferred deferred = mock(Deferred.class);
        when(request.getAuth()).thenReturn(PassThroughAuth.getInstance());
        when(deferredFactory.getDeferred()).thenReturn(deferred);

        // When
        dispatcher.dispatch(request, type, collectionType);

        // Then
        verify(dispatcher).send(any(RequestOrder.class), eq(deferred), eq(collectionType), eq(type));
    }

    private static class RequestDispatcherDummy extends RequestDispatcher {

        public RequestDispatcherDummy(ResponseProcessor processor, DeferredFactory deferredFactory) {
            super(processor, deferredFactory);
        }

        @Override
        protected <D> void send(RequestOrder request, Deferred<D> deferred, Class<D> resolveType,
                                @Nullable Class<?> parametrizedType) {
        }
    }
}
