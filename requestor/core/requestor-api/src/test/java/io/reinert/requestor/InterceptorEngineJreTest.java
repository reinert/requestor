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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link io.reinert.requestor.InterceptorEngine}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InterceptorEngineJreTest {

    @Mock private ResponseInterceptor responseInterceptor;
    @Mock private RequestInterceptor requestInterceptor;
    @Mock private InterceptorManagerImpl manager;

    private InterceptorEngine engine;

    @Before
    public void setUpManagerAndInitEngine() {
        when(manager.getRequestInterceptors()).thenReturn(Arrays.asList(requestInterceptor));
        when(manager.getResponseInterceptors()).thenReturn(Arrays.asList(responseInterceptor));
        engine = new InterceptorEngine(manager);
    }

    @Test
    public void interceptorRequest_AnyRequestBuilder_ShouldApplyAllRegisteredInterceptors() {
        // Given
        RequestInterceptorContext requestBuilder = mock(RequestInterceptorContext.class);

        // When
        engine.interceptRequest(requestBuilder);

        // Then
        verify(requestInterceptor).intercept(requestBuilder);
    }

    @Test
    public void interceptorResponse_AnyResponseBuilder_ShouldApplyAllRegisteredInterceptors() {
        // Given
        Request request = mock(Request.class);
        ResponseInterceptorContext response = mock(ResponseInterceptorContext.class);

        // When
        engine.interceptResponse(request, response);

        // Then
        verify(responseInterceptor).intercept(request, response);
    }
}
