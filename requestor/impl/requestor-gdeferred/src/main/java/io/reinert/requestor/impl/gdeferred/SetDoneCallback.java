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
package io.reinert.requestor.impl.gdeferred;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.requestor.RawResponseImpl;
import io.reinert.requestor.Response;

/**
 * DoneCallback&lt;Collection&lt;T&gt;&gt; that casts Collection&lt;T&gt; to Set&lt;T&gt;.
 *
 * @param <T> Type of Set's values
 *
 * @author Danilo Reinert
 */
public abstract class SetDoneCallback<T> implements DoneCallback<Collection<T>>, DoneCallbackForSet<T> {

    private static Logger logger = Logger.getLogger(SetDoneCallback.class.getName());

    @Override
    public void onDone(Set<T> result) { }

    @Override
    public void onDone(Collection<T> result) {
        try {
            onDone((Set<T>) result);
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Could not cast the result of type " + (result == null ? "null"
                    : result.getClass().getName()) + " to java.util.Set");
            throw e;
        }
    }

    /**
     * If you want to access the Response attributes, then override this method.
     *
     * @param response  the HTTP Response returned from the Request.
     */
    @SuppressWarnings("unchecked")
    public void onDone(Response<Set<T>> response) {
        if ((Response<?>) response instanceof RawResponseImpl) {
            onDone((Set<T>) response);
            return;
        }

        onDone(response.getPayload());
    }
}
