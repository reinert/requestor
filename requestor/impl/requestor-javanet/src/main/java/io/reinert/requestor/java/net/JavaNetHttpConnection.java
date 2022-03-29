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
package io.reinert.requestor.java.net;

import java.net.HttpURLConnection;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestOptions;

public class JavaNetHttpConnection implements HttpConnection {

    private final HttpURLConnection conn;
    private final Deferred<?> deferred;
    private final RequestOptions request;

    public JavaNetHttpConnection(HttpURLConnection conn, Deferred<?> deferred, RequestOptions request) {
        if (conn == null) throw new IllegalArgumentException("HttpURLConnection cannot be null.");
        this.conn = conn;
        this.deferred = deferred;
        this.request = request;
    }

    public void cancel() {
        cancel(new RequestCancelException(request, "Request was manually cancelled through the HttpConnection."));
    }

    public boolean isPending() {
        return deferred.isPending();
    }

    public HttpURLConnection getHttpUrlConnection() {
        return conn;
    }

    protected synchronized void cancel(RequestException exception) {
        if (isPending()) {
            conn.disconnect();
            deferred.reject(exception);
        }
    }
}
