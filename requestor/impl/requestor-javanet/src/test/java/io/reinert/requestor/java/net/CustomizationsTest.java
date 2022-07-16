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

import java.util.concurrent.Executors;
import java.util.logging.Level;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestLogger;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.java.ScheduledExecutorAsyncRunner;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for javanet customizations.
 */
public class CustomizationsTest extends JavaNetTest {

    private static final int TIMEOUT = 5_000;

    @Test(timeout = TIMEOUT * 2)
    public void testSetCookieStoreLevel() throws RequestException {
        final Session session = Requestor.newSession(
                new ScheduledExecutorAsyncRunner(Executors.newSingleThreadScheduledExecutor()));

        session.getLogger().setLevel(Level.INFO);

        Uri uri = Requestor.newUriBuilder()
                .scheme("https").host("httpbin.org").segment("cookies", "set")
                .queryParam("id", "a3fWa")
                .queryParam("Expires", "Thu, 21 Oct 2021 07:28:00 GMT")
                .queryParam("Secure")
                .queryParam("HttpOnly")
                .build();

        Response setCookieResponse = session.get(uri.toString(), String.class).await();

        System.out.println(setCookieResponse.getStatus());

        System.out.println(setCookieResponse.getSerializedPayload().asString());

        System.out.println(setCookieResponse.getHeaders().toString());

        String setCookieValue = setCookieResponse.getHeader("Set-Cookie");

        System.out.println(setCookieValue);

        System.out.println(session.retrieve("Cookie").toString());

        Response res = session.get("https://httpbin.org/cookies", String.class).await();

        System.out.println(res.getSerializedPayload().asString());

        Assert.assertTrue(res.<String>getPayload().contains("\"Cookie\": " + setCookieValue));
    }
}
