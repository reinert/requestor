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
 * Represents a subject-oriented client.
 *
 * @author Danilo Reinert
 */
public interface Service extends HasRequestOptions {

    /**
     * Get the main Session from which this Service was derived.
     *
     * @return  the main Session
     */
    Session getSession();

    /**
     * Get the Service's Store.
     *
     * It accesses the Session's Store, but does not persist on it by default.
     *
     * In order to persist on Session, we must set persist param to <code>true</code> when saving.
     *
     * @return  the Service's Store
     */
    Store getStore();
}
