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

public class FilterProcessingRequest extends AbstractProcessingRequest implements RequestFilterContext {

    private final RequestFilter filter;

    public FilterProcessingRequest(ProcessingRequest request, RequestFilter filter) {
        super(request);
        this.filter = filter;
    }

    @Override
    protected void process() {
        filter.filter(this);
    }
}