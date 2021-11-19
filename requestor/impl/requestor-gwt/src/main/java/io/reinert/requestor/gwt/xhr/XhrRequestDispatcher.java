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
package io.reinert.requestor.gwt.xhr;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.HttpMethod;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.ProgressEvent;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestDispatchException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestOptions;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.RequestProgressImpl;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.gwt.ResponseType;
import io.reinert.requestor.gwt.payload.SerializedJsPayload;

/**
 * Default implementation of {@link RequestDispatcher}.
 *
 * @author Danilo Reinert
 */
public class XhrRequestDispatcher extends RequestDispatcher {

    public XhrRequestDispatcher(RequestProcessor requestProcessor, ResponseProcessor responseProcessor,
                                DeferredPool.Factory deferredPoolFactory) {
        super(requestProcessor, responseProcessor, deferredPoolFactory);
    }

    @Override
    public native void scheduleRun(Runnable runnable, int delay) /*-{
        setTimeout($entry(function() {
            runnable.@java.lang.Runnable::run()();
        }), delay);
    }-*/;

    @Override
    protected <D> void send(final PreparedRequest request, final Deferred<D> deferred, PayloadType payloadType) {
        if (!deferred.isPending()) return;

        final HttpMethod httpMethod = request.getMethod();
        final String url = request.getUri().toString();
        final Headers headers = request.getHeaders();
        final SerializedPayload serializedPayload = request.getSerializedPayload();

        // Create XMLHttpRequest
        final XmlHttpRequest xmlHttpRequest = (XmlHttpRequest) XmlHttpRequest.create();

        // Open XMLHttpRequest
        try {
            xmlHttpRequest.open(httpMethod.getValue(), url);
        } catch (JavaScriptException e) {
            deferred.reject(new RequestPermissionException(request, url, e));
            return;
        }

        // Set withCredentials
        xmlHttpRequest.setWithCredentials(request.isWithCredentials());

        // Fulfill headers
        try {
            setHeaders(headers, xmlHttpRequest);
        } catch (JavaScriptException e)  {
            deferred.reject(new RequestAbortException(request, "Could not manipulate the XHR headers: " +
                    e.getMessage(), e));
            return;
        }

        // Set responseType
        PayloadType responsePayloadType = request.getResponsePayloadType();
        if (responsePayloadType != null) {
            xmlHttpRequest.setResponseType(ResponseType.of(responsePayloadType.getType()).getValue());
        }

        // Create RequestCallback
        final RequestCallback callback = getRequestCallback(request, xmlHttpRequest, deferred, payloadType);

        // Create the underlying request from gwt.http module
        final com.google.gwt.http.client.RequestorRequest gwtRequest = new com.google.gwt.http.client.RequestorRequest(
                xmlHttpRequest, request.getTimeout(), callback);

        // Properly configure XMLHttpRequest's onreadystatechange
        xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
            public void onReadyStateChange(com.google.gwt.xhr.client.XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XmlHttpRequest.DONE) {
                    xhr.clearOnReadyStateChange();
                    ((XmlHttpRequest) xhr).clearOnProgress();
                    gwtRequest.fireOnResponseReceived(callback);
                }
            }
        });

        // Set XMLHttpRequest's onabort if available binding to request's error
        xmlHttpRequest.setOnAbort(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.reject(new NetworkErrorException(request, new RequestProgressImpl(progress)));
            }
        });

        // Set XMLHttpRequest's onerror if available binding to request's error
        xmlHttpRequest.setOnError(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.reject(new NetworkErrorException(request, new RequestProgressImpl(progress)));
            }
        });

        // Set XMLHttpRequest's onprogress if available binding to request's progress
        xmlHttpRequest.setOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                deferred.notifyDownload(new RequestProgressImpl(progress));
            }
        });

        // Set XMLHttpRequest's upload onprogress if available binding to request's progress
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
            if (serializedPayload != null) {
                if (serializedPayload instanceof SerializedJsPayload) {
                    SerializedJsPayload gwtPayload = (SerializedJsPayload) serializedPayload;
                    if (gwtPayload.isString()) {
                        xmlHttpRequest.send(gwtPayload.getString());
                    } else {
                        xmlHttpRequest.send(gwtPayload.getObject());
                    }
                } else {
                    xmlHttpRequest.send(serializedPayload.getString());
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

    private <D> RequestCallback getRequestCallback(final RequestOptions requestOptions,
                                                   final XmlHttpRequest xhr,
                                                   final Deferred<D> deferred,
                                                   final PayloadType payloadType) {
        return new RequestCallback() {
            public void onResponseReceived(com.google.gwt.http.client.Request gwtRequest,
                                           com.google.gwt.http.client.Response gwtResponse) {
                final String responseType = xhr.getResponseType();

                SerializedJsPayload serializedPayload = null;

                if (responseType == null || responseType.isEmpty() ||
                        responseType.equalsIgnoreCase(ResponseType.TEXT.getValue())) {
                    serializedPayload = SerializedJsPayload.fromText(xhr.getResponseText());
                } else if (responseType.equalsIgnoreCase(ResponseType.BLOB.getValue())) {
                    serializedPayload = SerializedJsPayload.fromBlob(xhr.getResponse());
                } else if (responseType.equalsIgnoreCase(ResponseType.DOCUMENT.getValue())) {
                    serializedPayload = SerializedJsPayload.fromDocument(xhr.getResponse());
                } else if (responseType.equalsIgnoreCase(ResponseType.JSON.getValue())) {
                    serializedPayload = SerializedJsPayload.fromJson(xhr.getResponse());
                }

//                ResponseType.of(responseType)

                final RawResponse response = new RawResponse(
                        deferred,
                        Status.of(gwtResponse.getStatusCode()),
                        toHeaders(gwtResponse.getHeaders()),
                        payloadType,
                        serializedPayload);

                evalResponse(response);
            }

            public void onError(com.google.gwt.http.client.Request gwtRequest, Throwable exception) {
                if (exception instanceof com.google.gwt.http.client.RequestTimeoutException) {
                    // reject as timeout
                    com.google.gwt.http.client.RequestTimeoutException e =
                            (com.google.gwt.http.client.RequestTimeoutException) exception;
                    deferred.reject(new RequestTimeoutException(requestOptions, e.getTimeoutMillis()));
                } else {
                    // reject as generic request exception
                    deferred.reject(new RequestCancelException(requestOptions,
                            "Request has been cancelled after being sent. See previous exception.", exception));
                }
            }
        };
    }

    private void setHeaders(Headers headers, XmlHttpRequest xmlHttpRequest) throws JavaScriptException {
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                if (header != null) xmlHttpRequest.setRequestHeader(header.getName(), header.getValue());
            }
        }
    }

    private Headers toHeaders(com.google.gwt.http.client.Header[] headers) {
        if (headers.length > 0) {
            List<Header> headerList = new ArrayList<Header>();
            for (final com.google.gwt.http.client.Header header : headers) {
                headerList.add(Header.fromRawHeader(header.getName(), header.getValue()));
            }
            return new Headers(headerList);
        }
        return new Headers();
    }
}
