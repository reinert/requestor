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

import com.google.gwtmockito.GwtMockitoTestRunner;

import io.reinert.requestor.deferred.DeferredRequest;
import io.reinert.requestor.deferred.RequestPromise;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit tests of {@link RequestDispatcher}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class RequestDispatcherTest {

    @Mock private ResponseProcessor processor;
    private RequestDispatcher dispatcher;

    @Before
    public void setUp() {
        dispatcher = spy(new RequestDispatcherDummy(processor));
    }

    @Test
    public void dispatch_OneClass_ShouldCallSend() {
        // Given
        Class<Object> type = Object.class;
        SerializedRequest request = mock(SerializedRequestImpl.class);

        // When
        RequestPromise<Object> deferred = dispatcher.dispatch(request, type);

        // Then
        verify(dispatcher).send(request, (DeferredRequest<Object>) deferred, type);
    }

    @Test
    public void dispatch_TwoClasses_ShouldCallSend() {
        // Given
        Class<Collection> collectionType = Collection.class;
        Class<Object> type = Object.class;
        SerializedRequest request = mock(SerializedRequestImpl.class);

        // When
        RequestPromise<Collection<Object>> deferred = dispatcher.dispatch(request, type, collectionType);

        // Then
        verify(dispatcher).send(eq(request), eq((DeferredRequest<Collection<Object>>) deferred), eq(type),
                Matchers.<Class<Collection<Object>>>anyObject()); /* Should be eq(collectionType)
                                                                     but it was not possible due to generics */
    }

    class RequestDispatcherDummy extends RequestDispatcher {

        public RequestDispatcherDummy(ResponseProcessor processor) {
            super(processor);
        }

        @Override
        protected <T> void send(SerializedRequest request, DeferredRequest<T> deferred, Class<T> resultType) {
            // Do nothing
        }

        @Override
        protected <T, C extends Collection> void send(SerializedRequest request, DeferredRequest<C> deferred,
                                                      Class<T> resultType, Class<C> containerType) {
            // Do nothing
        }
    }
}
