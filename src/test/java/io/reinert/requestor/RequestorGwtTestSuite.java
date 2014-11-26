package io.reinert.requestor;

import com.google.gwt.junit.tools.GWTTestSuite;

import io.reinert.requestor.header.MultipleHeaderTest;
import io.reinert.requestor.header.QualityFactorHeaderTest;
import io.reinert.requestor.header.SimpleHeaderWithParameterTest;
import io.reinert.requestor.serialization.OverlaySerdesTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RequestorGwtTestSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Requestor GWT Test Suite");

        // Header
        suite.addTestSuite(QualityFactorHeaderTest.class);
        suite.addTestSuite(MultipleHeaderTest.class);
        suite.addTestSuite(SimpleHeaderWithParameterTest.class);

        // Serialization
        suite.addTestSuite(OverlaySerdesTest.class);

        // Requestor
        suite.addTestSuite(JsonAutoBeanGeneratorTest.class);
        suite.addTestSuite(JsonGwtJacksonGeneratorTest.class);

        return suite;
    }
}
