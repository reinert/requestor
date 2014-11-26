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
package io.reinert.requestor.test.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * A mock of {@link com.google.gwt.http.client.Response}.
 *
 * @author Danilo Reinert
 */
public class ResponseMock extends Response {

    private final String text;
    private final int statusCode;
    private final String statusText;
    private final Header[] headers;

    ResponseMock(String text, int statusCode, String statusText, Header[] headers) {
        this.text = text;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ResponseMock of(String text, int statusCode, String statusText, Header... headers) {
        return new ResponseMock(text, statusCode, statusText, headers);
    }

    @Override
    public String getHeader(String header) {
        if (header == null) throw new NullPointerException("Header param cannot be null.");
        if (header.isEmpty()) throw new IllegalArgumentException("Header param cannot be empty.");
        for (Header h : headers) {
            if (h.getName().equals(header)) return h.getValue();
        }
        return null;
    }

    @Override
    public Header[] getHeaders() {
        return headers;
    }

    @Override
    public String getHeadersAsString() {
        return Arrays.toString(headers);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public JavaScriptObject getData() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    /**
     * A builder of {@link ResponseMock}.
     */
    public static class Builder {
        private String text;
        private int statusCode;
        private String statusText;
        private final List<Header> headers = new ArrayList<Header>();

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Builder header(final String name, final String value) {
            headers.add(new Header() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getValue() {
                    return value;
                }

                @Override
                public String toString() {
                    return name + " = " + value;
                }
            });
            return this;
        }

        public Builder clearHeaders() {
            headers.clear();
            return this;
        }

        public ResponseMock build() {
            return new ResponseMock(text, statusCode, statusText, (Header[]) headers.toArray());
        }
    }
}
