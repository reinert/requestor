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

import java.util.Collection;

import io.reinert.requestor.deferred.Deferred;

/**
 * The final form of a request.
 * <p/>
 * When assuming this type, a request has already been processed and can only pass through an authentication before
 * been finally dispatched.
 * <p/>
 * This class has self dispatching capabilities through the #send method, which should be called only once.
 *
 * @author Danilo Reinert
 */
public interface RequestOrder extends SerializedRequest {

    boolean isWithCredentials();

    void setWithCredentials(boolean withCredentials);

    void setHeader(String name, String value);

    void setQueryParam(String name, String... values);

    void send();

    void abort(RawResponse response);

    void abort(RequestException error);

    <D, C extends Collection> RequestOrder copy(Class<D> resultType, Class<C> containerType, Deferred<C> deferred);

    <D> RequestOrder copy(Class<D> resultType, Deferred<D> deferred);

}
