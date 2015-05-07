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

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Unit tests of {@link io.reinert.requestor.InterceptorManager}.
 */
public class InterceptorManagerTest {

    private InterceptorManager manager = new InterceptorManager();

    @Test(expected = UnsupportedOperationException.class)
    public void getRequestInterceptors_ShouldReturnAnImmutableList() {
        // Given
        RequestInterceptor requestInterceptor = mock(RequestInterceptor.class);
        manager.addRequestInterceptor(requestInterceptor);

        // When
        List<RequestInterceptor> interceptors = manager.getRequestInterceptors();

        // Then
        assertEquals(interceptors.size(), 1);
        assertSame(requestInterceptor, manager.getRequestInterceptors().get(0));
        interceptors.add(mock(RequestInterceptor.class)); // throw UnsupportedOperationException
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getResponseInterceptors_ShouldReturnAnImmutableList() {
        // Given
        ResponseInterceptor responseInterceptor = mock(ResponseInterceptor.class);
        manager.addResponseInterceptor(responseInterceptor);

        // When
        List<ResponseInterceptor> interceptors = manager.getResponseInterceptors();

        // Then
        assertEquals(interceptors.size(), 1);
        assertSame(responseInterceptor, manager.getResponseInterceptors().get(0));
        interceptors.add(mock(ResponseInterceptor.class)); // throw UnsupportedOperationException
    }

    @Test
    public void wrappedManagerShouldNotBeAffected() {
        // Given
        RequestInterceptor requestInterceptor = mock(RequestInterceptor.class);
        ResponseInterceptor responseInterceptor = mock(ResponseInterceptor.class);
        manager.addRequestInterceptor(requestInterceptor);
        manager.addResponseInterceptor(responseInterceptor);

        InterceptorManager wrappingManager = new InterceptorManager(manager);
        RequestInterceptor requestInterceptor2 = mock(RequestInterceptor.class);
        ResponseInterceptor responseInterceptor2 = mock(ResponseInterceptor.class);

        // When
        wrappingManager.addRequestInterceptor(requestInterceptor2);
        wrappingManager.addResponseInterceptor(responseInterceptor2);

        // Then
        assertEquals(manager.getRequestInterceptors().size(), 1);
        assertEquals(manager.getResponseInterceptors().size(), 1);
        assertEquals(wrappingManager.getRequestInterceptors().size(), 2);
        assertEquals(wrappingManager.getResponseInterceptors().size(), 2);
    }
}
