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

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reinert.requestor.core.header.LinkHeader;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * Default IncomingResponse implementation.
 *
 * @author Danilo Reinert
 */
public class IncomingResponseImpl implements IncomingResponse {

    private final RawResponse response;
    private final Headers headers;
    private final LinkHeader linkHeader;

    public IncomingResponseImpl(RawResponse response) {
        this.response = response;
        headers = new Headers(response.getHeaders());
        linkHeader = (LinkHeader) headers.get("Link");
    }

    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    public boolean hasHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    public Iterable<Link> getLinks() {
        return linkHeader != null ? linkHeader.getLinks() : Collections.<Link>emptyList();
    }

    public boolean hasLink(String relation) {
        return linkHeader != null && linkHeader.hasLink(relation);
    }

    public Link getLink(String relation) {
        return linkHeader != null ? linkHeader.getLink(relation) : null;
    }

    public Headers getHeaders() {
        return headers;
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

    public boolean exists(String key, Object value) {
        return response.exists(key, value);
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
        return getFuture(response.getDeferred().getResponseBodyLock(), new Callable<SerializedPayload>() {
            public SerializedPayload call() {
                return response.getSerializedPayload();
            }
        }, new Callable<Boolean>() {
            public Boolean call() {
                return response.isLoaded();
            }
        });
    }

    public <T> Future<T> getPayload() {
        return getFuture(response.getDeferred().getResponseLock(), new Callable<T>() {
            public T call() {
                return response.getPayload();
            }
        }, new Callable<Boolean>() {
            public Boolean call() {
                return response.isDeserialized();
            }
        });
    }

    private <T> Future<T> getFuture(final AsyncRunner.Lock lock, final Callable<T> result,
                                    final Callable<Boolean> doneCondition) {
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
                final long startTime = System.currentTimeMillis();

                while (!(cancelled && deferred.isRejected() && isDoneCondition())) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    lock.await(unit.toMillis(timeout - elapsed));

                    elapsed = System.currentTimeMillis() - startTime;
                    if (timeout > 0 && elapsed >= timeout) {
                        throw new TimeoutException("The timeout of " + timeout + "ms has expired.");
                    }
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
