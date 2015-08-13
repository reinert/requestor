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
package io.reinert.requestor.auth;

import io.reinert.requestor.Deferred;
import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.Promise;
import io.reinert.requestor.RequestProgress;

/**
 * Use it in the case you want to create a special deferred without exposing Promise type in external projects.
 *
 * @param <T> Type of the resolved object
 *
 * @author Danilo Reinert
 */
abstract class NullDeferred<T> implements Deferred<T> {
    @Override
    public void notifyDownload(RequestProgress progress) {
    }

    @Override
    public void notifyUpload(RequestProgress progress) {
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
    }

    @Override
    public Promise<T> getPromise() {
        return null;
    }
}
