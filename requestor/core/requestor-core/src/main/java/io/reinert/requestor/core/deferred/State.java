/*
 * Copyright 2013-2018 Ray Tsang
 * Copyright 2014-2021 Danilo Reinert
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
package io.reinert.requestor.core.deferred;

enum State {

    /**
     * The Deferred is still pending - it could be created, submitted for execution, or currently running, but not
     * yet finished.
     */
    PENDING,

    /**
     * The Deferred has finished running and a failure occurred. Thus, the Deferred is rejected.
     */
    REJECTED,

    /**
     * The Deferred has finished running successfully. Thus, the Deferred is resolved.
     */
    RESOLVED
}
