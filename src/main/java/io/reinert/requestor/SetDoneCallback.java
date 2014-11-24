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
package io.reinert.requestor;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.gdeferred.DoneCallback;

/**
 * DoneCallback<Collection<T>> that casts Collection<T> to Set<T>.
 *
 * @param <T> Type of Set's values
 *
 * @author Danilo Reinert
 */
public abstract class SetDoneCallback<T> implements DoneCallback<Collection<T>>, DoneCallbackForSet<T> {

    private static Logger logger = Logger.getLogger(SetDoneCallback.class.getName());

    @Override
    public void onDone(Collection<T> result) {
        try {
            onDone((Set<T>) result);
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Could not cast the result of type " + result.getClass().getName()
                    + " to java.util.Set");
            throw e;
        }
    }
}
