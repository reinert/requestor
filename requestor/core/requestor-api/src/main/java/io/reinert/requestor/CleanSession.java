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

import io.reinert.requestor.serialization.misc.TextSerializer;
import io.reinert.requestor.serialization.misc.VoidSerializer;

/**
 * A clean session that handles only string data.
 *
 * @author Danilo Reinert
 */
public class CleanSession extends Session {

    public CleanSession() {
        super();
    }

    public CleanSession(RequestDispatcherFactory requestDispatcherFactory, DeferredFactory deferredFactory) {
        super(requestDispatcherFactory, deferredFactory);
    }

    protected void configure() {
        register(VoidSerializer.getInstance());
        register(TextSerializer.getInstance());
    }
}
