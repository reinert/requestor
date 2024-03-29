/*
 * Copyright 2015-2023 Danilo Reinert
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
package io.reinert.requestor.core;

import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * <p>The final form of a request.</p>
 *
 * <p>When assuming this type, a request has already been processed and can only pass through an authentication before
 * been finally dispatched.</p>
 *
 * <p>This class has self dispatching capabilities through the #send method, which should be called only once.</p>
 *
 * @author Danilo Reinert
 */
public interface PreparedRequest extends SerializedRequest, HasHeaders {

    boolean isWithCredentials();

    PayloadType getResponsePayloadType();

    RequestDispatcher getDispatcher();

    Deferred<?> getDeferred();

    void setWithCredentials(boolean withCredentials);

    void setQueryParam(String name, String... values);

    void setTimeout(int timeoutMillis);

    MutableSerializedRequest getMutableCopy();

    void send();

    void abort(RawResponse response);

    void abort(RequestAbortException error);

    ConnectionPreparer getConnectionPreparer();

    void setConnectionPreparer(ConnectionPreparer preparer);

    interface ConnectionPreparer {
        void prepareConnection(HttpConnection connection);
    }
}
