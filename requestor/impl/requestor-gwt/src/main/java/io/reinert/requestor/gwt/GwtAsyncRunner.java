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
package io.reinert.requestor.gwt;

import io.reinert.requestor.core.AsyncRunner;

/**
 * GWT implementation for AsyncRunner using setTimeout.
 *
 * @author Danilo Reinert
 */
public class GwtAsyncRunner implements AsyncRunner {

    public static GwtAsyncRunner INSTANCE = new GwtAsyncRunner();

    @Override
    public native void run(Runnable runnable, int delayMillis) /*-{
        setTimeout($entry(function() {
            runnable.@java.lang.Runnable::run()();
        }), delayMillis);
    }-*/;

    @Override
    public void sleep(int millis) {
        // no-op (unnecessary to block the thread in JS environment)
    }
}
