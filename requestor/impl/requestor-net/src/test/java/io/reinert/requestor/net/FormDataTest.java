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
package io.reinert.requestor.net;

import io.reinert.requestor.core.FormData;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class FormDataTest extends NetTest {

    private static final int TIMEOUT = 10_000;

    @Test(timeout = TIMEOUT)
    public void testFormDataUrlEncoded() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        final FormData data = FormData.builder()
                .append("string", "value")
                .append("int", 1)
                .append("long", 10L)
                .append("double", 1.5)
                .append("boolean", true)
                .build();

        session.req("https://httpbin.org/post")
                .contentType("application/x-www-form-urlencoded")
                .payload(data)
                .post(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"Content-Type\": \"application/x-www-form-urlencoded\""));
                    Assert.assertTrue(body.contains("\"string\": \"value\""));
                    Assert.assertTrue(body.contains("\"int\": \"1\""));
                    Assert.assertTrue(body.contains("\"long\": \"10\""));
                    Assert.assertTrue(body.contains("\"double\": \"1.5\""));
                    Assert.assertTrue(body.contains("\"boolean\": \"true\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }
}
