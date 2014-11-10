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
package com.google.gwt.http.client;

/**
 * The primary interface a caller must implement to receive a response to a
 * {@link com.google.gwt.http.client.Request}.
 *
 * <h3>Required Module</h3>
 * Modules that use this interface should inherit
 * <code>com.google.gwt.http.HTTP</code>.
 *
 * {@gwt.include com/google/gwt/examples/http/InheritsExample.gwt.xml}
 */
public interface RequestCallbackWithProgress extends RequestCallback {

    /**
     * Called when a pending {@link com.google.gwt.http.client.Request} triggers a progress event.
     *
     * @param requestProgress the progress data
     */
    void onProgress(RequestProgress requestProgress);
}
