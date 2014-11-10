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

import com.google.gwt.junit.tools.GWTTestSuite;

import io.reinert.requestor.test.books.RestTest;
import io.reinert.requestor.uri.UriBuilderTest;

import junit.framework.Test;

/**
 * @author Danilo Reinert
 */
public class RequestorGwtTestSuite {

    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("Requestor Test Suite");

        suite.addTestSuite(UriBuilderTest.class);

        suite.addTestSuite(MultipleHeaderTest.class);
        suite.addTestSuite(QualityFactorHeaderTest.class);
        suite.addTestSuite(SimpleHeaderWithParameterTest.class);

        suite.addTestSuite(UriBuilderTest.class);
        suite.addTestSuite(RequestTest.class);
        suite.addTestSuite(ContentTypeAcceptPatternsTest.class);
//        suite.addTestSuite(SerializerAndDeserializerMatchTest.class);
        suite.addTestSuite(SerializerAndDeserializerPrecedenceTest.class);
        suite.addTestSuite(MultipleSerdesByClassTest.class);

        suite.addTestSuite(RestTest.class);

        suite.addTestSuite(JsonGwtJacksonGeneratorTest.class);
        suite.addTestSuite(JsonAutoBeanGeneratorTest.class);

        return suite;
    }
}
