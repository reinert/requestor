package io.reinert.requestor.serialization;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

/**
 * Test suite for serialization package.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JsonBooleanSerdesTest.class,
        JsonNumberSerdesTest.class,
        JsonStringSerdesTest.class})
public class SerializationTestSuite extends TestSuite {
}
