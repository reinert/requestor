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

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Default implementation for {@link ServerConnection}.
 *
 * @author Danilo Reinert
 */
public class ServerConnectionImpl implements ServerConnection {

    @Override
    public void sendRequest(RequestBuilder.Method method, String url, String data, RequestCallback callback)
            throws RequestException {
        new RequestBuilder(method, url).sendRequest(data, callback);
    }

    @Override
    public void sendRequest(int timeout, @Nullable String user, @Nullable String password, @Nullable Headers headers,
                            RequestBuilder.Method method, String url, String data, RequestCallback callback)
            throws RequestException {
        final RequestBuilder requestBuilder = new RequestBuilder(method, url);
        if (timeout > 0) requestBuilder.setTimeoutMillis(timeout);
        if (user != null) requestBuilder.setUser(user);
        if (password != null) requestBuilder.setPassword(password);
        if (user != null && password != null) requestBuilder.setIncludeCredentials(true);
        if (headers != null) {
            for (Header header : headers) {
                requestBuilder.setHeader(header.getName(), header.getValue());
            }
        }
        requestBuilder.sendRequest(data, callback);
    }
}
