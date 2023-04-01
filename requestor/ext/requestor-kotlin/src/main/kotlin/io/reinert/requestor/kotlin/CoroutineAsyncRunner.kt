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

import java.lang.Runnable
import java.util.concurrent.ConcurrentHashMap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel

import io.reinert.requestor.core.AsyncRunner

class CoroutineAsyncRunner(private val scope: CoroutineScope,
                           private val dispatcher: CoroutineDispatcher? = null) : AsyncRunner {

    class Lock(private val scope: CoroutineScope) : AsyncRunner.Lock {

        private var channel: Channel<Unit>? = null
        private var waiting = false

        override fun await(timeout: Long) {
            if (channel == null) channel = Channel(0)
            waiting = true
            runBlocking(scope.coroutineContext) {
                channel!!.receive()
                waiting = false
            }
        }

        override fun isAwaiting(): Boolean {
            return waiting;
        }

        override fun signalAll() {
            channel?.trySend(Unit)
        }

    }

    private val jobs : MutableSet<Job> = ConcurrentHashMap.newKeySet()

    fun join() {
        runBlocking {
            jobs.forEach { it.join() }
        }
    }

    override fun run(runnable: Runnable?, delayMillis: Long) {
        val job = scope.launch {
            if (dispatcher != null) {
                withContext(dispatcher) {
                    if (delayMillis > 0) delay(delayMillis.toLong())
                    runnable?.run()
                }
            } else {
                if (delayMillis > 0) delay(delayMillis.toLong())
                runnable?.run()
            }
        }

        jobs += job

        scope.launch {
            if (dispatcher != null) {
                withContext(dispatcher) {
                    job.join()
                    jobs.remove(job)
                }
            } else {
                job.join()
                jobs.remove(job)
            }
        }
    }

    override fun sleep(millis: Long) {
        // no-op
    }

    override fun shutdown() {
        // no-op
    }

    override fun isShutdown(): Boolean {
        return false
    }

    override fun getLock(): AsyncRunner.Lock {
        return Lock(scope)
    }
}