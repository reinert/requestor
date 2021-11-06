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
package io.reinert.gdeferred.impl;

public class RequestorDeferred<D, F, P> extends DeferredObject<D, F, P> {

    public RequestorDeferred<D, F, P> getUnresolvedCopy() {
        RequestorDeferred<D, F, P> copy = new RequestorDeferred<D, F, P>();
        copy.getDoneCallbacks().addAll(this.getDoneCallbacks());
        copy.getFailCallbacks().addAll(this.getFailCallbacks());
        copy.getProgressCallbacks().addAll(this.getProgressCallbacks());
        return copy;
    }
}
