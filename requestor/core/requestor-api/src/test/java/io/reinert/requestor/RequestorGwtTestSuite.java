/*
 * Copyright 2015 Danilo Reinert
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

import com.google.gwt.junit.tools.GWTTestSuite;

import io.reinert.requestor.serialization.OverlaySerdesTest;
import io.reinert.requestor.uri.UriBuilderTest;
import io.reinert.requestor.uri.UriParserTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RequestorGwtTestSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Requestor GWT Test Suite");

        // Uri
        suite.addTestSuite(UriBuilderTest.class);
        suite.addTestSuite(UriParserTest.class);

        // Serialization
        suite.addTestSuite(OverlaySerdesTest.class);

        // Requestor
        suite.addTestSuite(JsonAutoBeanGeneratorTest.class);

        return suite;
    }
}
