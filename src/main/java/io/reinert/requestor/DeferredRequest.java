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

import java.util.ArrayList;
import java.util.logging.Level;

import io.reinert.gdeferred.Deferred;
import io.reinert.gdeferred.ProgressCallback;
import io.reinert.gdeferred.Promise;
import io.reinert.gdeferred.impl.DeferredObject;

/**
 * Abstract deferred for Requests.
 *
 * @param <T>   Expected type in {@link RequestPromise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public abstract class DeferredRequest<T> extends DeferredObject<T, Throwable, RequestProgress>
        implements RequestPromise<T> , Deferred<T, Throwable, RequestProgress> {

    private final ResponseProcessor processor;
    private Connection connection;
    private ArrayList<ProgressCallback<RequestProgress>> uploadProgressCallbacks;

    public DeferredRequest(ResponseProcessor processor) {
        if (processor == null)
            throw new NullPointerException("ResponseProcessor cannot be null.");
        this.processor = processor;
    }

    protected abstract DeserializedResponse<T> process(ResponseProcessor processor, Request request,
                                                       SerializedResponse response);

    public DeferredRequest<T> resolve(Request request, SerializedResponse response) {
        DeserializedResponse<T> deserializedResponse = process(processor, request, response);
        super.resolve(deserializedResponse.getPayload());
        return this;
    }

    @Override
    public Deferred<T, Throwable, RequestProgress> reject(Throwable reject) {
        // If the http connection is still opened, then close it
        if (connection != null && connection.isPending())
            connection.cancel();
        return super.reject(reject);
    }

    @Override
    public Promise<T, Throwable, RequestProgress> uploadProgress(ProgressCallback<RequestProgress> callback) {
        getUploadProgressCallbacks().add(callback);
        return this;
    }

    public Deferred<T, Throwable, RequestProgress> notifyUpload(RequestProgress progress) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot notify upload progress");
        }

        triggerUploadProgress(progress);

        return this;
    }

    protected void triggerUploadProgress(RequestProgress progress) {
        for (ProgressCallback<RequestProgress> callback : getUploadProgressCallbacks()) {
            try {
                triggerProgress(callback, progress);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a ProgressCallback", e);
            }
        }
    }

    protected ArrayList<ProgressCallback<RequestProgress>> getUploadProgressCallbacks() {
        if (uploadProgressCallbacks == null)
            uploadProgressCallbacks = new ArrayList<ProgressCallback<RequestProgress>>();
        return uploadProgressCallbacks;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }
}
