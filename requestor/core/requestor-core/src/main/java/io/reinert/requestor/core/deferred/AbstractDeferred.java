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

import java.util.ArrayList;
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
abstract class AbstractDeferred<D, F, P, U> {

    protected F rejectResult;
    protected D resolveResult;
    protected State state = State.PENDING;

    protected final List<DoneCallback<D>> doneCallbacks;
    protected final List<FailCallback<F>> failCallbacks;
    protected List<ProgressCallback<P>> progressCallbacks;
    protected List<ProgressCallback<U>> upProgressCallbacks;

    public AbstractDeferred() {
        this.doneCallbacks = new ArrayList<DoneCallback<D>>();
        this.failCallbacks = new ArrayList<FailCallback<F>>();
    }

    protected AbstractDeferred(List<DoneCallback<D>> doneCallbacks,
                               List<FailCallback<F>> failCallbacks,
                               List<ProgressCallback<P>> progressCallbacks,
                               List<ProgressCallback<U>> upProgressCallbacks) {
        this.doneCallbacks = doneCallbacks;
        this.failCallbacks = failCallbacks;
        this.progressCallbacks = progressCallbacks;
        this.upProgressCallbacks = upProgressCallbacks;
    }

    public AbstractDeferred<D, F, P, U> done(DoneCallback<D> callback) {
        doneCallbacks.add(callback);
        if (isResolved()) triggerDone(callback, resolveResult);
        return this;
    }

    public AbstractDeferred<D, F, P, U> fail(FailCallback<F> callback) {
        failCallbacks.add(callback);
        if (isRejected()) triggerFail(callback, rejectResult);
        return this;
    }

    public boolean isPending() {
        return state == State.PENDING;
    }

    public boolean isRejected() {
        return state == State.REJECTED;
    }

    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    public AbstractDeferred<D, F, P, U> progress(ProgressCallback<P> callback) {
        getProgressCallbacks().add(callback);
        return this;
    }

    public AbstractDeferred<D, F, P, U> upProgress(ProgressCallback<U> callback) {
        getUpProgressCallbacks().add(callback);
        return this;
    }

    public State state() {
        return state;
    }

    protected List<DoneCallback<D>> getDoneCallbacks() {
        return doneCallbacks;
    }

    protected List<FailCallback<F>> getFailCallbacks() {
        return failCallbacks;
    }

    protected List<ProgressCallback<P>> getProgressCallbacks() {
        if (progressCallbacks == null)
            progressCallbacks = new ArrayList<ProgressCallback<P>>();
        return progressCallbacks;
    }

    protected List<ProgressCallback<U>> getUpProgressCallbacks() {
        if (upProgressCallbacks == null)
            upProgressCallbacks = new ArrayList<ProgressCallback<U>>();
        return upProgressCallbacks;
    }

    protected void triggerDone(D resolved) {
        for (DoneCallback<D> callback : doneCallbacks) {
            triggerDone(callback, resolved);
        }
    }

    protected void triggerDone(DoneCallback<D> callback, D resolved) {
        callback.onDone(resolved);
    }

    protected void triggerFail(F rejected) {
        for (FailCallback<F> callback : failCallbacks) {
            triggerFail(callback, rejected);
        }
    }

    protected void triggerFail(FailCallback<F> callback, F rejected) {
        callback.onFail(rejected);
    }

    protected void triggerProgress(P progress) {
        if (progressCallbacks != null) {
            for (ProgressCallback<P> callback : progressCallbacks) {
                triggerProgress(callback, progress);
            }
        }
    }

    protected void triggerUpProgress(U progress) {
        if (upProgressCallbacks != null) {
            for (ProgressCallback<U> callback : upProgressCallbacks) {
                triggerUpProgress(callback, progress);
            }
        }
    }

    protected void triggerProgress(ProgressCallback<P> callback, P progress) {
        callback.onProgress(progress);
    }

    protected void triggerUpProgress(ProgressCallback<U> callback, U progress) {
        callback.onProgress(progress);
    }
}
