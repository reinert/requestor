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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reinert.requestor.core.deferred.ThreadUtil;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * Default IncomingResponse implementation.
 *
 * @author Danilo Reinert
 */
public class IncomingResponseImpl implements IncomingResponse {

    private final RawResponse response;

    public IncomingResponseImpl(RawResponse response) {
        this.response = response;
    }

    public String getHeader(String headerName) {
        return response.getHeader(headerName);
    }

    public boolean hasHeader(String headerName) {
        return response.hasHeader(headerName);
    }

    public String getContentType() {
        return response.getContentType();
    }

    public Iterable<Link> getLinks() {
        return response.getLinks();
    }

    public boolean hasLink(String relation) {
        return response.hasLink(relation);
    }

    public Link getLink(String relation) {
        return response.getLink(relation);
    }

    public Headers getHeaders() {
        return response.getHeaders();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public HttpStatus getStatus() {
        return response.getStatus();
    }

    public PayloadType getPayloadType() {
        return response.getPayloadType();
    }

    public RequestOptions getRequestOptions() {
        return response.getRequestOptions();
    }

    public <T> T retrieve(String key) {
        return response.retrieve(key);
    }

    public RawResponse save(String key, Object value) {
        return response.save(key, value);
    }

    public RawResponse save(String key, Object value, Level level) {
        return response.save(key, value, level);
    }

    public boolean exists(String key) {
        return response.exists(key);
    }

    public boolean isEquals(String key, Object value) {
        return response.isEquals(key, value);
    }

    public boolean remove(String key) {
        return response.remove(key);
    }

    public void clear() {
        response.clear();
    }

    public Session getSession() {
        return response.getSession();
    }

    public Future<SerializedPayload> getSerializedPayload() {
        return getFuture(new Callable<SerializedPayload>() {
            public SerializedPayload call() {
                return response.getSerializedPayload();
            }
        }, new Callable<Boolean>() {
            public Boolean call() {
                return response.isLoaded();
            }
        });
    }

    public Future<Payload> getPayload() {
        return getFuture(new Callable<Payload>() {
            public Payload call() {
                return response.getPayload();
            }
        }, new Callable<Boolean>() {
            public Boolean call() {
                return response.isDeserialized();
            }
        });
    }

    private <T> Future<T> getFuture(final Callable<T> result, final Callable<Boolean> doneCondition) {
        final Deferred<?> deferred = response.getDeferred();
        return new Future<T>() {
            private boolean cancelled;

            public boolean cancel(boolean mayInterruptIfRunning) {
                if (isDoneCondition()) return false;
                if (!mayInterruptIfRunning) return false;

                cancelled = true;
                return true;
            }

            public boolean isCancelled() {
                return cancelled;
            }

            public boolean isDone() {
                return cancelled || isDoneCondition();
            }

            public T get() throws InterruptedException, ExecutionException {
                try {
                    return get(0, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    throw new ExecutionException(e);
                }
            }

            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                    TimeoutException {
                checkInvalidStates();

                if (!isDoneCondition()) {
                    ThreadUtil.waitSafely(response, unit.toMillis(timeout), new Callable<Boolean>() {
                        public Boolean call() {
                            return !isDoneCondition() && deferred.isPending();
                        }
                    });
                }

                checkInvalidStates();

                try {
                    return result.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private void checkInvalidStates() throws ExecutionException {
                if (cancelled) {
                    throw new CancellationException("Future was cancelled.");
                }

                if (deferred.isRejected()) {
                    throw new ExecutionException(deferred.getRejectResult());
                }
            }

            private boolean isDoneCondition() {
                try {
                    return doneCondition.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
