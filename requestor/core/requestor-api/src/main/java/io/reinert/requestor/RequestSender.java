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

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.header.Header;

/**
 * A {@link RequestBuilder} capable of sending itself.
 *
 * @see RequestSenderImpl
 *
 * @author Danilo Reinert
 */
public interface RequestSender extends RequestBuilder, HasHttpSendMethods {

    @Override
    RequestSender contentType(String mediaType);

    @Override
    RequestSender accept(String mediaType);

    @Override
    RequestSender header(String header, String value);

    @Override
    RequestSender header(Header header);

    @Override
    RequestSender auth(Authentication auth);

    @Override
    RequestSender timeout(int timeoutMillis);

    @Override
    RequestSender payload(Object object) throws IllegalArgumentException;

    @Override
    RequestSender responseType(ResponseType responseType);

}
