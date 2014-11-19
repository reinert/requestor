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

import javax.annotation.Nullable;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestCallbackWithProgress;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestPermissionException;
import com.google.gwt.xhr.client.ProgressEvent;
import com.google.gwt.xhr.client.ProgressHandler;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import io.reinert.requestor.header.Header;

/**
 * Default implementation for {@link ServerConnection}.
 *
 * @author Danilo Reinert
 */
public class ServerConnectionImpl implements ServerConnection {

    @Override
    public RequestInProgress sendRequest(int timeout, @Nullable String user, @Nullable String password,
                                         @Nullable Headers headers, String httpMethod, String url, String data,
                                         final RequestCallback callback) throws RequestException {
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

        final RequestInProgress request = new RequestInProgress(xmlHttpRequest, timeout, callback);

        // Must set the onreadystatechange handler before calling send().
        xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
            public void onReadyStateChange(XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                    xhr.clearOnReadyStateChange();
                    request.fireOnResponseReceived(callback);
                }
            }
        });

        // ADDED BY REQUESTOR
        if (callback instanceof RequestCallbackWithProgress) {
            final RequestCallbackWithProgress pCallback = (RequestCallbackWithProgress) callback;
            xmlHttpRequest.setOnProgress(new ProgressHandler() {
                @Override
                public void onProgress(ProgressEvent progress) {
                    pCallback.onProgress(new RequestProgressImpl(progress));
                }
            });
        }

        try {
            xmlHttpRequest.send(data);
        } catch (JavaScriptException e) {
            throw new RequestException(e.getMessage());
        }

        return request;
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
