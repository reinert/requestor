/*
 * Copyright 2022 Danilo Reinert
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

/**
 * It allows registering callbacks to store events.
 *
 * @author Danilo Reinert
 */
interface StoreManager extends Saver {

    <T> T retrieve(String key);

    StoreManager save(String key, Object value);

    boolean exists(String key);

    boolean exists(String key, Object value);

    boolean remove(String key);

    void clear();

    StoreManager onSaved(String key, Store.SaveCallback callback);

    StoreManager onRemoved(String key, Store.RemoveCallback callback);
}
