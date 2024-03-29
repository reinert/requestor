/*
 * Copyright 2021-2022 Danilo Reinert
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

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RequestorGwtTestSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Requestor GWT Test Suite");

        suite.addTestSuite(CallbackGwtTest.class);

        suite.addTestSuite(PollingGwtTest.class);

        suite.addTestSuite(RetryGwtTest.class);

        suite.addTestSuite(SpecialTypeResponsesGwtTest.class);

        suite.addTestSuite(RestServiceGwtTest.class);

        suite.addTestSuite(BaseServiceGwtTest.class);

        suite.addTestSuite(RequestFilterGwtTest.class);

        suite.addTestSuite(RequestInterceptorGwtTest.class);

        suite.addTestSuite(ResponseFilterGwtTest.class);

        suite.addTestSuite(ResponseInterceptorGwtTest.class);

        return suite;
    }
}
