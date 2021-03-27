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
 * Unit tests of {@link FilterEngine}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterEngineJreTest {

    @Mock private ResponseFilter responseFilter;
    @Mock private RequestFilter requestFilter;
    @Mock private FilterManagerImpl manager;

    private FilterEngine engine;

    @Before
    public void setUpManagerAndInitEngine() {
        when(manager.getRequestFilters()).thenReturn(Arrays.asList(requestFilter));
        when(manager.getResponseFilters()).thenReturn(Arrays.asList(responseFilter));
        engine = new FilterEngine(manager);
    }

    @Test
    public void filterRequest_AnyRequestBuilder_ShouldApplyAllRegisteredFilters() {
        // Given
        RequestInProcess request = mock(RequestInProcess.class);

        // When
        engine.filterRequest(request);

        // Then
        verify(requestFilter).filter(request);
    }

    @Test
    public void filterResponse_AnyResponseBuilder_ShouldApplyAllRegisteredFilters() {
        // Given
        Request request = mock(Request.class);
        ResponseFilterContext response = mock(ResponseFilterContext.class);

        // When
        engine.filterResponse(request, response);

        // Then
        verify(responseFilter).filter(request, response);
    }
}
