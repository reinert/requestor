/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor.gdeferred;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.gdeferred.DoneCallback;

/**
 * DoneCallback<Collection<T>> that casts Collection<T> to List<T>.
 *
 * @param <T> Type of List's values
 *
 * @author Danilo Reinert
 */
public abstract class ListDoneCallback<T> implements DoneCallback<Collection<T>>, DoneCallbackForList<T> {

    private static Logger logger = Logger.getLogger(ListDoneCallback.class.getName());

    @Override
    public void onDone(Collection<T> result) {
        try {
            onDone((List<T>) result);
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Could not cast the result of type " + (result == null ? "null"
                    : result.getClass().getName()) + " to java.util.List");
            throw e;
        }
    }
}
