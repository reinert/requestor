/*
 * Copyright 2023 Danilo Reinert
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
package io.reinert.requestor.kotlin

import io.reinert.requestor.java.net.Requestor

import kotlinx.coroutines.*
import kotlin.test.*

class RequestorKotlinTest {

    @Test
    fun `make a simple request`() {
        var payload = ""
        runBlocking {
            val runner = CoroutineAsyncRunner(this, Dispatchers.IO)
            val session = Requestor.newSession(runner)

            session.req("https://httpbin.org/get")
                    .get(String::class.java)
                    .onLoad { res -> payload = res.getPayload() }
        }

        assertTrue { payload != "" }
    }

}
