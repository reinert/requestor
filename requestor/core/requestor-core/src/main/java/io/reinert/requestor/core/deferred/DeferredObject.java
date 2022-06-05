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

import java.util.List;

/**
 * Abstract implementation of Deferred.
 *
 * @param <D> The type of the result received when the deferred is done
 * @param <F> The type of the result received when the deferred failed
 * @param <P> The type of the progress notification
 *
 * @author Ray Tsang
 * @author Danilo Reinert
 */
class DeferredObject<D, F, P, U> extends AbstractDeferred<D, F, P, U> {

    public DeferredObject() {
        super();
    }

    protected DeferredObject(List<DoneCallback<D>> doneCallbacks,
                             List<FailCallback<F>> failCallbacks,
                             List<ProgressCallback<P>> progressCallbacks,
                             List<ProgressCallback<U>> upProgressCallbacks) {
        super(doneCallbacks, failCallbacks, progressCallbacks, upProgressCallbacks);
    }

    //    @Override
    public DeferredObject<D, F, P, U> notifyDownload(final P progress) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot notify progress");
        }

        triggerProgress(progress);
        return this;
    }

    //    @Override
    public DeferredObject<D, F, P, U> notifyUpload(final U progress) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot notify progress");
        }

        triggerUpProgress(progress);
        return this;
    }

    //    @Override
    public DeferredObject<D, F, P, U> reject(final F reject) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot reject again");
        }

        state = State.REJECTED;
        rejectResult = reject;

        triggerFail(reject);

        synchronized (this) {
            ThreadUtil.notifyAll(this);
        }

        return this;
    }

    //    @Override
    public DeferredObject<D, F, P, U> resolve(final D resolve) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot resolve again");
        }

        state = State.RESOLVED;
        resolveResult = resolve;

        triggerDone(resolve);

        synchronized (this) {
            ThreadUtil.notifyAll(this);
        }

        return this;
    }

    public DeferredObject<D, F, P, U> replicate() {
        return new DeferredObject<D, F, P, U>(doneCallbacks, failCallbacks, progressCallbacks, upProgressCallbacks);
    }
}
