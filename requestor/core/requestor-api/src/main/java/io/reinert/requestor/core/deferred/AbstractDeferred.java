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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract implementation of Deferred.
 *
 * @param <D> The type of the result received when the promise is done
 * @param <F> The type of the result received when the promise failed
 * @param <P> The type of the progress notification
 */
abstract class AbstractDeferred<D, F, P> {

    protected final Logger log = Logger.getLogger(String.valueOf(AbstractDeferred.class));

    protected F rejectResult;
    protected D resolveResult;

    protected final List<DoneCallback<D>> doneCallbacks;
    protected final List<FailCallback<F>> failCallbacks;
    protected List<ProgressCallback<P>> progressCallbacks;
    protected List<ProgressCallback<P>> upProgressCallbacks;

    public AbstractDeferred() {
        this.doneCallbacks = new ArrayList<DoneCallback<D>>();
        this.failCallbacks = new ArrayList<FailCallback<F>>();
    }

    protected AbstractDeferred(List<DoneCallback<D>> doneCallbacks,
                               List<FailCallback<F>> failCallbacks,
                               List<ProgressCallback<P>> progressCallbacks,
                               List<ProgressCallback<P>> upProgressCallbacks) {
        this.doneCallbacks = doneCallbacks;
        this.failCallbacks = failCallbacks;
        this.progressCallbacks = progressCallbacks;
        this.upProgressCallbacks = upProgressCallbacks;
    }

    public AbstractDeferred<D, F, P> done(DoneCallback<D> callback) {
        doneCallbacks.add(callback);
        return this;
    }

    public AbstractDeferred<D, F, P> fail(FailCallback<F> callback) {
        failCallbacks.add(callback);
        return this;
    }

    public AbstractDeferred<D, F, P> progress(ProgressCallback<P> callback) {
        getUpProgressCallbacks().add(callback);
        return this;
    }

    public AbstractDeferred<D, F, P> upProgress(ProgressCallback<P> callback) {
        getProgressCallbacks().add(callback);
        return this;
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

    protected List<ProgressCallback<P>> getUpProgressCallbacks() {
        if (upProgressCallbacks == null)
            upProgressCallbacks = new ArrayList<ProgressCallback<P>>();
        return upProgressCallbacks;
    }

    protected void triggerDone(D resolved) {
        for (DoneCallback<D> callback : doneCallbacks) {
            try {
                triggerDone(callback, resolved);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a DoneCallback", e);
            }
        }
    }

    protected void triggerDone(DoneCallback<D> callback, D resolved) {
        callback.onDone(resolved);
    }

    protected void triggerFail(F rejected) {
        for (FailCallback<F> callback : failCallbacks) {
            try {
                triggerFail(callback, rejected);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a FailCallback", e);
            }
        }
    }

    protected void triggerFail(FailCallback<F> callback, F rejected) {
        callback.onFail(rejected);
    }

    protected void triggerProgress(P progress) {
        if (progressCallbacks != null) {
            for (ProgressCallback<P> callback : progressCallbacks) {
                try {
                    triggerProgress(callback, progress);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "An uncaught exception occurred in a ProgressCallback", e);
                }
            }
        }
    }

    protected void triggerUpProgress(P progress) {
        if (upProgressCallbacks != null) {
            for (ProgressCallback<P> callback : upProgressCallbacks) {
                try {
                    triggerProgress(callback, progress);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "An uncaught exception occurred in a up ProgressCallback", e);
                }
            }
        }
    }

    protected void triggerProgress(ProgressCallback<P> callback, P progress) {
        callback.onProgress(progress);
    }
}
