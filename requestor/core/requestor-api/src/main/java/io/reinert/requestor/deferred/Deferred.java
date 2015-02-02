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
package io.reinert.requestor.deferred;

import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.Response;

/**
 * A Promise with resolving capabilities.
 *
 * @param <T> The type of the promise value
 *
 * @author Danilo Reinert
 */
public interface Deferred<T> {

    void resolve(Response<T> response);

    void reject(RequestException error);

    void notifyDownload(RequestProgress progress);

    void notifyUpload(RequestProgress progress);

    void setHttpConnection(HttpConnection connection);

    Promise<T> getPromise();

}
