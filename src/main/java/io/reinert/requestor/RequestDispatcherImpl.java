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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.xhr.client.ProgressEvent;
import com.google.gwt.xhr.client.ProgressHandler;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import io.reinert.requestor.header.Header;

/**
 * Default implementation of {@link RequestDispatcher}.
 *
 * @author Danilo Reinert
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    private final SerializationEngine serializationEngine;
    private final FilterEngine filterEngine;

    public RequestDispatcherImpl(SerializationEngine serializationEngine, FilterEngine filterEngine) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
    }

    @Override
    public <T> RequestPromise<T> send(RequestBuilder request, Class<T> responseType) {
        final DeferredSingleResult<T> deferred = new DeferredSingleResult<T>(serializationEngine, responseType);
        send(request, deferred);
        return deferred;
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> send(RequestBuilder request,
                                                                        Class<T> responseType,
                                                                        Class<C> containerType) {
        final DeferredCollectionResult<T> deferred = new DeferredCollectionResult<T>(serializationEngine, responseType,
                containerType);
        send(request, deferred);
        return deferred;
    }

    private <D> void send(final Request request, final DeferredRequest<D> deferred) throws RequestException {
        final String httpMethod = request.getMethod();
        final String url = request.getUrl();
        final String user = request.getUser();
        final String password = request.getPassword();
        final Headers headers = request.getHeaders();

        // Exception not caught purposefully because ongoing serialization is not a request failure
        final String body = serializationEngine.serialize(request.getPayload(), request.getContentType(), url, headers);

        // Create XMLHttpRequest
        XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();

        // Open XMLHttpRequest
        try {
            open(httpMethod, url, user, password, xmlHttpRequest);
        } catch (JavaScriptException e) {
            RequestPermissionException requestPermissionException = new RequestPermissionException(url);
            requestPermissionException.initCause(new RequestException(e.getMessage()));
            deferred.reject(requestPermissionException);
        }

        // Fulfill headers
        try {
            setHeaders(headers, xmlHttpRequest);
        } catch (JavaScriptException e) {
            deferred.reject(new RequestException(e.getMessage()));
        }

        // Set withCredentials if necessary
        if (user != null) {
            xmlHttpRequest.setWithCredentials(true);
        }

        // Create RequestCallback
        final RequestCallback callback = getRequestCallback(request, deferred);

        // Create the underlying request from gwt.http module
        final com.google.gwt.http.client.Request gwtRequest = new com.google.gwt.http.client.Request(xmlHttpRequest,
                request.getTimeout(), callback);

        // Properly configure XMLHttpRequest's onreadystatechange
        xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
            public void onReadyStateChange(XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                    xhr.clearOnReadyStateChange();
                    gwtRequest.fireOnResponseReceived(callback);
                }
            }
        });

        // Set XMLHttpRequest's onprogress if available binding to promise's progress
        xmlHttpRequest.setOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.notify(new RequestProgressImpl(progress));
            }
        });

        // Pass the connection to the deferred to enable it to cancel the request if necessary
        deferred.setConnection(getConnection(gwtRequest));

        // Send the request
        try {
            xmlHttpRequest.send(body);
        } catch (JavaScriptException e) {
            deferred.reject(new RequestDispatchException(e.getMessage()));
        }
    }

    private Connection getConnection(final com.google.gwt.http.client.Request gwtRequest) {
        return new Connection() {
            @Override
            public void cancel() {
                gwtRequest.cancel();
            }

            @Override
            public boolean isPending() {
                return gwtRequest.isPending();
            }
        };
    }

    private <D> RequestCallback getRequestCallback(final Request request, final DeferredRequest<D> deferred) {
        return new RequestCallback() {
            @Override
            public void onResponseReceived(com.google.gwt.http.client.Request gwtRequest, Response gwtResponse) {
                final ResponseImpl responseWrapper = new ResponseImpl(gwtResponse);

                // Execute filters on this response
                filterEngine.applyResponseFilters(request, responseWrapper);

                if (gwtResponse.getStatusCode() / 100 == 2) {
                    // Resolve if response is 2xx
                    deferred.resolve(request, responseWrapper);
                } else {
                    // Reject as unsuccessful response if response isn't 2xx
                    deferred.reject(new UnsuccessfulResponseException(request, responseWrapper));
                }
            }

            @Override
            public void onError(com.google.gwt.http.client.Request gwtRequest, Throwable exception) {
                if (exception instanceof com.google.gwt.http.client.RequestTimeoutException) {
                    // Reject as timeout
                    com.google.gwt.http.client.RequestTimeoutException e =
                            (com.google.gwt.http.client.RequestTimeoutException) exception;
                    deferred.reject(new RequestTimeoutException(request,
                            e.getTimeoutMillis()));
                } else {
                    // Reject as generic request exception
                    deferred.reject(new RequestException(exception));
                }
            }
        };
    }

    private void open(String httpMethod, String url, String user, String password, XMLHttpRequest xmlHttpRequest)
            throws JavaScriptException {
        if (user != null && password != null) {
            xmlHttpRequest.open(httpMethod, url, user, password);
        } else if (user != null) {
            xmlHttpRequest.open(httpMethod, url, user);
        } else {
            xmlHttpRequest.open(httpMethod, url);
        }
    }

    private void setHeaders(Headers headers, XMLHttpRequest xmlHttpRequest) throws JavaScriptException {
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                xmlHttpRequest.setRequestHeader(header.getName(), header.getValue());
            }
        }
    }
}
