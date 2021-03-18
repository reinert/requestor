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

import java.util.Collection;

/**
 * An HTTP Invoker bound to a Resource type.
 *
 * @author Danilo Reinert
 */
public interface ResourceInvoker<R, I> {

    Promise<Collection<R>> get(String... params);

    Promise<R> get(I id, String... params);

    Promise<Void> post(R resource);

    Promise<Void> put(I id, R resource);

    Promise<Void> delete(I id);

    // TODO: make a patch request filtering informed fields in serialization
//    Promise<Void> patch(I id, R resource, String... fields);

    RequestInvoker req();

    RequestInvoker req(I id);

}
