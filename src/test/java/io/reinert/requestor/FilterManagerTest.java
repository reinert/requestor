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
 * Unit tests of {@link FilterManager}.
 */
public class FilterManagerTest {

    private FilterManager manager = new FilterManager();

    @Test(expected = UnsupportedOperationException.class)
    public void getRequestFilters_ShouldReturnAnImmutableList() {
        // Given
        RequestFilter requestFilter = mock(RequestFilter.class);
        manager.addRequestFilter(requestFilter);

        // When
        List<RequestFilter> filters = manager.getRequestFilters();

        // Then
        assertEquals(filters.size(), 1);
        assertSame(requestFilter, manager.getRequestFilters().get(0));
        filters.add(mock(RequestFilter.class)); // throw UnsupportedOperationException
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getResponseFilters_ShouldReturnAnImmutableList() {
        // Given
        ResponseFilter responseFilter = mock(ResponseFilter.class);
        manager.addResponseFilter(responseFilter);

        // When
        List<ResponseFilter> filters = manager.getResponseFilters();

        // Then
        assertEquals(filters.size(), 1);
        assertSame(responseFilter, manager.getResponseFilters().get(0));
        filters.add(mock(ResponseFilter.class)); // throw UnsupportedOperationException
    }
}
