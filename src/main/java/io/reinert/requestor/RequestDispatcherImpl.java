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

import com.google.gwt.core.client.JavaScriptException;
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

    @Override
    public Connection send(final Request request, SerializationEngine engine, final ConnectionCallback callback)
            throws RequestException {
        final String httpMethod = request.getMethod();
        final String url = request.getUrl();
        final String user = request.getUser();
        final String password = request.getPassword();
        final Headers headers = request.getHeaders();
        final String body = engine.serialize(request.getPayload(), request.getContentType(), url, headers);

        XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();

        try {
            if (user != null && password != null) {
                xmlHttpRequest.open(httpMethod, url, user, password);
            } else if (user != null) {
                xmlHttpRequest.open(httpMethod, url, user);
            } else {
                xmlHttpRequest.open(httpMethod, url);
            }
        } catch (JavaScriptException e) {
            RequestPermissionException requestPermissionException = new RequestPermissionException(url);
            requestPermissionException.initCause(new RequestException(e.getMessage()));
            throw requestPermissionException;
        }

        setHeaders(headers, xmlHttpRequest);

        if (user != null) {
            xmlHttpRequest.setWithCredentials(true);
        }

        final com.google.gwt.http.client.Request gwtRequest = new com.google.gwt.http.client.Request(xmlHttpRequest,
                request.getTimeout(), callback);

        xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
            public void onReadyStateChange(XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                    xhr.clearOnReadyStateChange();
                    gwtRequest.fireOnResponseReceived(callback);
                }
            }
        });

        xmlHttpRequest.setOnProgress(new ProgressHandler() {
            @Override
            public void onProgress(ProgressEvent progress) {
                callback.onProgress(new RequestProgressImpl(progress));
            }
        });

        try {
            xmlHttpRequest.send(body);
        } catch (JavaScriptException e) {
            throw new RequestException(e.getMessage());
        }

        return new Connection() {

            @Override
            public void cancel() {
                gwtRequest.cancel();
            }

            @Override
            public Request getRequest() {
                return request;
            }

            @Override
            public boolean isPending() {
                return gwtRequest.isPending();
            }
        };
    }

    private void setHeaders(Headers headers, XMLHttpRequest xmlHttpRequest) throws RequestException {
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                try {
                    xmlHttpRequest.setRequestHeader(header.getName(), header.getValue());
                } catch (JavaScriptException e) {
                    throw new RequestException(e.getMessage());
                }
            }
        }
    }
}
