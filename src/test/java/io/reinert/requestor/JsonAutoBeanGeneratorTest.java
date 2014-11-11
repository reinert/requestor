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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.test.mock.ResponseMock;
import io.reinert.requestor.test.mock.ServerStub;

/**
 * @author Danilo Reinert
 */
public class JsonAutoBeanGeneratorTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorWithAutoBeanTest";
    }

    public void testGeneratedSingleDeserialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final String uri = "/animal";

        final String serialized = "{\"name\":\"Stuart\",\"age\":3}";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).get(Animal.class)
                .done(new DoneCallback<Animal>() {
                    @Override
                    public void onDone(Animal animal) {
                        callbackDoneCalled[0] = true;
                        assertTrue(isEquals(stuart, animal));
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedListDeserialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final Animal march = animalProvider.get();
        march.setName("March");
        march.setAge(5);

        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).get(Animal.class, List.class)
                .done(new DoneCallback<Collection<Animal>>() {
                    @Override
                    public void onDone(Collection<Animal> animals) {
                        callbackDoneCalled[0] = true;
                        List<Animal> list = (List<Animal>) animals;
                        assertTrue(Arrays.equals(list.toArray(), list.toArray()));
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedCustomListDeserialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final Animal march = animalProvider.get();
        march.setName("March");
        march.setAge(5);

        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).get(Animal.class, List.class)
                .done(new DoneCallback<Collection<Animal>>() {
                    @Override
                    public void onDone(Collection<Animal> animals) {
                        callbackDoneCalled[0] = true;
                        List<Animal> list = (List<Animal>) animals;
                        assertTrue(Arrays.equals(list.toArray(), list.toArray()));
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedSingleSerialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final String uri = "/animal";

        final String serialized = "{\"name\":\"Stuart\",\"age\":3}";

        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).payload(stuart).post()
                .done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void ignored) {
                        callbackDoneCalled[0] = true;
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
        assertEquals(serialized, ServerStub.getRequestData(uri).getData());
    }

    public void testGeneratedListSerialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final Animal march = animalProvider.get();
        march.setName("March");
        march.setAge(5);

        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).payload(list).post()
                .done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void ignored) {
                        callbackDoneCalled[0] = true;
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
        assertEquals(serialized, ServerStub.getRequestData(uri).getData());
    }

    public void testGeneratedCustomListSerialization() {
        final Requestor requestor = getRequestor();
        final Provider<Animal> animalProvider = requestor.getProvider(Animal.class);

        final Animal stuart = animalProvider.get();
        stuart.setName("Stuart");
        stuart.setAge(3);

        final Animal march = animalProvider.get();
        march.setName("March");
        march.setAge(5);

        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).payload(list).post()
                .done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void ignored) {
                        callbackDoneCalled[0] = true;
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
        assertEquals(serialized, ServerStub.getRequestData(uri).getData());
    }

    private Requestor getRequestor() {
        ServerStub.clearStub();
        return GWT.create(Requestor.class);
    }

    @Json({"app*/json*", "*/javascript*"})
    interface Animal {
        Integer getAge();
        String getName();
        void setAge(Integer age);
        void setName(String name);
    }

    private boolean isEquals(Animal a, Animal b) {
        return a.getName().equals(b.getName()) && a.getAge().equals(b.getAge());
    }
}
