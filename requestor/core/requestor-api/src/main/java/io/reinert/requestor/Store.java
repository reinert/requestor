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
 * A place to save/retrieve any object by key.
 *
 * @author Danilo Reinert
 */
public interface Store {

    /**
     * Queries an object associated with the given key.
     *
     * @param key The key associated with the data
     * @param <T> The type to typecast the object returned
     * @return The object associated with the key. <code>Null</code> otherwise.
     */
    <T> T get(String key);

    /**
     * Inserts the value into the store associated with the key.
     * Being a request scope store, the data will be available during the request/response lifecycle only.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     */
    void save(String key, Object value);

    /**
     * Inserts the value into the store associated with the key.
     * Being a request scope store, the data will be available during the request/response lifecycle only.
     * If you want to persist it in the session lifecycle, then set the boolean param to <code>True</code>.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     * @param persist Whether the data should be persisted in the underlying stores or not
     */
    void save(String key, Object value, boolean persist);

    /**
     * Checks if there's any data associated with the given key.
     *
     * @param key The key associated with the data
     * @return <code>True</code> if there's any data associated with the key. <code>False</code> otherwise.
     */
    boolean has(String key);

    /**
     * Removes the data associated with this key.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     *
     * @param key The key associated with the data
     * @return <code>True</code> if any data was removed. <code>False</code> otherwise.
     */
    boolean delete(String key);

    /**
     * Clears the data associated with this Store.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     */
    void clear();

}
