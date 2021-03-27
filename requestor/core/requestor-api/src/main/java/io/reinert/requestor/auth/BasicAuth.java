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
package io.reinert.requestor.auth;

import com.google.gwt.core.client.JavaScriptException;

import io.reinert.requestor.PreparedRequest;
import io.reinert.requestor.RequestDispatcher;

/**
 * HTTP Basic Authentication implementation. <br/>
 * Beyond username and password, this class accepts a withCredentials boolean, useful for CORS requests.
 *
 * @author Danilo Reinert
 */
public class BasicAuth implements Auth {

    private final String user;
    private final String password;
    private final boolean withCredentials;

    public BasicAuth(String user, String password) {
        this(user, password, false);
    }

    public BasicAuth(String user, String password, boolean withCredentials) {
        this.user = user;
        this.password = password;
        this.withCredentials = withCredentials;
    }

    @Override
    public void auth(PreparedRequest preparedRequest, RequestDispatcher dispatcher) {
        try {
            preparedRequest.setHeader("Authorization", "Basic " + btoa(user + ":" + password));
        } catch (JavaScriptException e) {
            throw new UnsupportedOperationException("It was not possible to encode credentials using browser's " +
                    "native btoa. The browser does not support this operation.", e);
        }
        preparedRequest.setWithCredentials(withCredentials);
        preparedRequest.send();
    }

    private static native String btoa(String str) /*-{
        return $wnd.btoa(str);
    }-*/;
}
