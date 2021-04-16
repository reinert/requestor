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
package io.reinert.requestor;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;

import io.reinert.requestor.header.Header;
import io.reinert.requestor.payload.PayloadType;

/**
 * Default implementation of {@link RequestDispatcher}.
 *
 * @author Danilo Reinert
 */
public class XhrRequestDispatcher extends RequestDispatcher {

    public XhrRequestDispatcher(RequestProcessor requestProcessor, ResponseProcessor responseProcessor,
                                DeferredFactory deferredFactory) {
        super(requestProcessor, responseProcessor, deferredFactory);
    }

    @Override
    protected <D> void send(PreparedRequest request, final Deferred<D> deferred, PayloadType payloadType) {
        final HttpMethod httpMethod = request.getMethod();
        final String url = request.getUri().toString();
        final Headers headers = request.getHeaders();
        final Payload payload = request.getSerializedPayload();
        final ResponseType responseType = request.getResponseType();

        // Create XMLHttpRequest
        final XMLHttpRequest xmlHttpRequest = (XMLHttpRequest) XMLHttpRequest.create();

        // Open XMLHttpRequest
        try {
            xmlHttpRequest.open(httpMethod.getValue(), url);
        } catch (JavaScriptException e) {
            RequestPermissionException requestPermissionException = new RequestPermissionException(request, url);
            requestPermissionException.initCause(new RequestException(request, e.getMessage()));
            deferred.reject(requestPermissionException);
            return;
        }

        // Set withCredentials
        xmlHttpRequest.setWithCredentials(request.isWithCredentials());

        // Fulfill headers
        try {
            setHeaders(headers, xmlHttpRequest);
        } catch (JavaScriptException e) {
            deferred.reject(new RequestException(request, "Could not manipulate the XHR headers: " +
                    e.getMessage()));
            return;
        }

        // Set responseType
        xmlHttpRequest.setResponseType(responseType.getValue());

        // Create RequestCallback
        final RequestCallback callback = getRequestCallback(request, xmlHttpRequest, deferred, payloadType);

        // Create the underlying request from gwt.http module
        final com.google.gwt.http.client.RequestorRequest gwtRequest = new com.google.gwt.http.client.RequestorRequest(
                xmlHttpRequest, request.getTimeout(), callback);

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
                // TODO(reinert): should we check if the promise is pending before notifying to avoid exceptions?
                deferred.notifyDownload(new RequestProgressImpl(progress));
            }
        });

        // Set XMLHttpRequest's upload onprogress if available binding to promise's progress
        xmlHttpRequest.setUploadOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                // TODO(reinert): should we check if the promise is pending before notifying to avoid exceptions?
                deferred.notifyUpload(new RequestProgressImpl(progress));
            }
        });

        // Pass the connection to the deferred to enable it to cancel the request if necessary (RECOMMENDED)
        deferred.setHttpConnection(getConnection(gwtRequest));

        // Send the request
        try {
            if (payload != null) {
                if (payload.isString() != null) {
                    xmlHttpRequest.send(payload.isString());
                } else {
                    xmlHttpRequest.send(payload.isJavaScriptObject());
                }
            } else {
                xmlHttpRequest.send();
            }
        } catch (JavaScriptException e) {
            deferred.reject(new RequestDispatchException(request, "Could not send the XHR: " + e.getMessage()));
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

    private <D> RequestCallback getRequestCallback(final Request request,
                                                   final XMLHttpRequest xhr,
                                                   final Deferred<D> deferred,
                                                   final PayloadType payloadType) {
        return new RequestCallback() {
            public void onResponseReceived(com.google.gwt.http.client.Request gwtRequest,
                                           com.google.gwt.http.client.Response gwtResponse) {
                final String responseType = xhr.getResponseType();

                Payload payload = null;

                if (responseType.isEmpty() || responseType.equalsIgnoreCase(ResponseType.TEXT.getValue())) {
                    payload = Payload.fromText(xhr.getResponseText());
                } else if (responseType.equalsIgnoreCase(ResponseType.BLOB.getValue())) {
                    payload = Payload.fromBlob(xhr.getResponse());
                } else if (responseType.equalsIgnoreCase(ResponseType.DOCUMENT.getValue())) {
                    payload = Payload.fromDocument(xhr.getResponse());
                } else if (responseType.equalsIgnoreCase(ResponseType.JSON.getValue())) {
                    payload = Payload.fromJson(xhr.getResponse());
                }

                final RawResponse response = new RawResponse(
                        request,
                        Status.of(gwtResponse.getStatusCode()),
                        new Headers(gwtResponse.getHeaders()),
                        ResponseType.of(responseType),
                        payload,
                        payloadType
                );

                evalResponse(request, deferred, response);
            }

            public void onError(com.google.gwt.http.client.Request gwtRequest, Throwable exception) {
                if (exception instanceof com.google.gwt.http.client.RequestTimeoutException) {
                    // reject as timeout
                    com.google.gwt.http.client.RequestTimeoutException e =
                            (com.google.gwt.http.client.RequestTimeoutException) exception;
                    deferred.reject(new RequestTimeoutException(request, e.getTimeoutMillis()));
                } else {
                    // reject as generic request exception
                    deferred.reject(new RequestException(request, exception));
                }
            }
        };
    }

    private void setHeaders(Headers headers, XMLHttpRequest xmlHttpRequest) throws JavaScriptException {
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                if (header != null) xmlHttpRequest.setRequestHeader(header.getName(), header.getValue());
            }
        }
    }
}