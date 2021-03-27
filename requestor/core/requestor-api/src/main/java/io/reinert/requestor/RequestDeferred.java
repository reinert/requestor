/*
 * Copyright 2021 Danilo Reinert
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

/**
 * A deferred object capable of resolving/rejecting request promises.
 *
 * @param <R> The type of the request
 *
 * @author Danilo Reinert
 */
public interface RequestDeferred<R extends Request> {

    void resolve(R request);

    void reject(RawResponse response);

    void reject(RequestException error);

}
