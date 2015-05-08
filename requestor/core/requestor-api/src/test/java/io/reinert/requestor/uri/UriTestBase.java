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
package io.reinert.requestor.uri;

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.fakes.FakeProvider;

import org.junit.Before;

/**
 * Base test class for every test that needs uri components.
 *
 * @author Danilo Reinert
 */
public class UriTestBase {

    @Before
    public void setUp() {
        GwtMockito.useProviderForType(UrlCodec.class, new FakeProvider<UrlCodec>() {
            @Override
            public UrlCodec getFake(Class<?> aClass) {
                return new UrlCodecMock();
            }
        });
        GwtMockito.useProviderForType(Buckets.class, new FakeProvider<Buckets>() {
            @Override
            public Buckets getFake(Class<?> aClass) {
                return new BucketsMock();
            }
        });
        GwtMockito.useProviderForType(UriBuilder.class, new FakeProvider<UriBuilder>() {
            @Override
            public UriBuilder getFake(Class<?> aClass) {
                return new UriBuilderImpl();
            }
        });
    }
}
