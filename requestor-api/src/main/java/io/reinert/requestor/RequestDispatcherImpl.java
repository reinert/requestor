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

import javax.annotation.Nullable;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;

import io.reinert.requestor.deferred.DeferredRequest;
import io.reinert.requestor.header.Header;

/**
 * Default implementation of {@link RequestDispatcher}.
 *
 * @author Danilo Reinert
 */
public class RequestDispatcherImpl extends RequestDispatcher {

    public RequestDispatcherImpl(ResponseProcessor processor, DeferredRequestFactory deferredFactory) {
        super(processor, deferredFactory);
    }

    @Override
    protected <T> void send(SerializedRequest request, DeferredRequest<T> deferred, Class<T> resultType) {
        doSend(request, deferred, resultType, null);
    }

    @Override
    protected <T, C extends Collection> void send(SerializedRequest request, DeferredRequest<C> deferred,
                                                  Class<T> resultType, Class<C> containerType) {
        doSend(request, deferred, containerType, resultType);
    }

    protected <D> void doSend(SerializedRequest request, final DeferredRequest<D> deferred, Class<D> resolveType,
                              @Nullable Class<?> parametrizedType) {
        final String httpMethod = request.getMethod();
        final String url = request.getUrl();
        final String user = request.getUser();
        final String password = request.getPassword();
        final Headers headers = request.getHeaders();
        final Payload payload = request.getPayload();
        final ResponseType responseType = request.getResponseType();

        // Create XMLHttpRequest
        XMLHttpRequest xmlHttpRequest = (XMLHttpRequest) XMLHttpRequest.create();

        // Open XMLHttpRequest
        try {
            open(httpMethod, url, user, password, xmlHttpRequest);
        } catch (JavaScriptException e) {
            RequestPermissionException requestPermissionException = new RequestPermissionException(url);
            requestPermissionException.initCause(new RequestException(e.getMessage()));
            deferred.rejectPromise(requestPermissionException);
        }

        // Fulfill headers
        try {
            setHeaders(headers, xmlHttpRequest);
        } catch (JavaScriptException e) {
            deferred.rejectPromise(new RequestException(e.getMessage()));
        }

        // Set withCredentials if necessary
        if (user != null) {
            xmlHttpRequest.setWithCredentials(true);
        }

        // Set responseType
        xmlHttpRequest.setResponseType(responseType.getValue());

        // Create RequestCallback
        final RequestCallback callback = getRequestCallback(request, xmlHttpRequest, deferred, resolveType,
                parametrizedType);

        // Create the underlying request from gwt.http module
        final com.google.gwt.http.client.Request gwtRequest = new com.google.gwt.http.client.Request(xmlHttpRequest,
                request.getTimeout(), callback);

        // Properly configure XMLHttpRequest's onreadystatechange
        xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
            public void onReadyStateChange(com.google.gwt.xhr.client.XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                    xhr.clearOnReadyStateChange();
                    ((XMLHttpRequest) xhr).clearOnProgress();
                    gwtRequest.fireOnResponseReceived(callback);
                }
            }
        });

        // Set XMLHttpRequest's onprogress if available binding to promise's progress
        xmlHttpRequest.setOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.notifyDownload(new RequestProgressImpl(progress));
            }
        });

        // Set XMLHttpRequest's upload onprogress if available binding to promise's progress
        xmlHttpRequest.setUploadOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.notifyUpload(new RequestProgressImpl(progress));
            }
        });

        // Pass the connection to the deferred to enable it to cancel the request if necessary (RECOMMENDED)
        deferred.setHttpConnection(getConnection(gwtRequest));

        // Send the request
        try {
            if (payload.isString() != null) {
                xmlHttpRequest.send(payload.isString());
            } else if (payload.isJavaScriptObject() != null) {
                xmlHttpRequest.send(payload.isJavaScriptObject());
            } else {
                xmlHttpRequest.send();
            }
        } catch (JavaScriptException e) {
            deferred.rejectPromise(new RequestDispatchException(e.getMessage()));
        }
    }

    private HttpConnection getConnection(final com.google.gwt.http.client.Request gwtRequest) {
        return new HttpConnection() {
            public void cancel() {
                gwtRequest.cancel();
            }

            public boolean isPending() {
                return gwtRequest.isPending();
            }
        };
    }

    private <D> RequestCallback getRequestCallback(final Request request, final XMLHttpRequest xhr,
                                                   final DeferredRequest<D> deferred, final Class<D> resolveType,
                                                   final Class<?> parametrizedType) {
        return new RequestCallback() {
            public void onResponseReceived(com.google.gwt.http.client.Request gwtRequest, Response gwtResponse) {
                final String responseType = xhr.getResponseType();
                final Payload payload = responseType.isEmpty() || responseType.equalsIgnoreCase("text") ?
                        new Payload(xhr.getResponseText()) : new Payload(xhr.getResponse());
                final SerializedResponseImpl response = new SerializedResponseImpl(gwtResponse.getStatusText(),
                        gwtResponse.getStatusCode(), new Headers(gwtResponse.getHeaders()),
                        ResponseType.of(responseType), payload);

                if (gwtResponse.getStatusCode() / 100 == 2) {
                    // Resolve if response is 2xx
                    final ResponseProcessor processor = getResponseProcessor();
                    @SuppressWarnings("unchecked")  // Ok, this is ugly
                    final DeserializedResponse<D> re = parametrizedType != null ?
                            (DeserializedResponse<D>) processor.process(request, response, parametrizedType,
                                    (Class<? extends Collection>) resolveType) :
                            processor.process(request, response, resolveType);
                    deferred.resolvePromise(re.getPayload());
                } else {
                    // rejectPromise as unsuccessful response if response isn't 2xx
                    deferred.rejectPromise(new UnsuccessfulResponseException(request, response));
                }
            }

            public void onError(com.google.gwt.http.client.Request gwtRequest, Throwable exception) {
                if (exception instanceof com.google.gwt.http.client.RequestTimeoutException) {
                    // rejectPromise as timeout
                    com.google.gwt.http.client.RequestTimeoutException e =
                            (com.google.gwt.http.client.RequestTimeoutException) exception;
                    deferred.rejectPromise(new RequestTimeoutException(request, e.getTimeoutMillis()));
                } else {
                    // rejectPromise as generic request exception
                    deferred.rejectPromise(new RequestException(exception));
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
