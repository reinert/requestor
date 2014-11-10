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

import org.turbogwt.core.collections.JsArrayList;

/**
 * @author Danilo Reinert
 */
public class JsonGwtJacksonGeneratorTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testGeneratedSingleDeserialization() {
        final Animal stuart = new Animal("Stuart", 3);

        final Requestor requestor = getRequestor();

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
                        assertEquals(stuart, animal);
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedArrayListDeserialization() {
        final Animal stuart = new Animal("Stuart", 3);
        final Animal march = new Animal("March", 5);
        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final Requestor requestor = getRequestor();

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).get(Animal.class, ArrayList.class)
                .done(new DoneCallback<Collection<Animal>>() {
                    @Override
                    public void onDone(Collection<Animal> animals) {
                        callbackDoneCalled[0] = true;
                        ArrayList<Animal> arrayList = (ArrayList<Animal>) animals;
                        assertTrue(Arrays.equals(list.toArray(), arrayList.toArray()));
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedCustomListDeserialization() {
        final Animal stuart = new Animal("Stuart", 3);
        final Animal march = new Animal("March", 5);
        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final Requestor requestor = getRequestor();

        final String uri = "/animals";

        final String serialized = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).get(Animal.class, JsArrayList.class)
                .done(new DoneCallback<Collection<Animal>>() {
                    @Override
                    public void onDone(Collection<Animal> animals) {
                        callbackDoneCalled[0] = true;
                        JsArrayList<Animal> arrayList = (JsArrayList<Animal>) animals;
                        assertTrue(Arrays.equals(list.toArray(), arrayList.toArray()));
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
    }

    public void testGeneratedSingleSerialization() {
        final Animal stuart = new Animal("Stuart", 3);

        final Requestor requestor = getRequestor();

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

    public void testGeneratedArrayListSerialization() {
        final Animal stuart = new Animal("Stuart", 3);
        final Animal march = new Animal("March", 5);
        final List<Animal> list = new ArrayList<Animal>();
        list.add(stuart); list.add(march);

        final Requestor requestor = getRequestor();

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
        final Animal stuart = new Animal("Stuart", 3);
        final Animal march = new Animal("March", 5);
        final List<Animal> list = new JsArrayList<Animal>();
        list.add(stuart); list.add(march);

        final Requestor requestor = getRequestor();

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

    /**
     * Class to auto-generate serializer.
     */
    @Json({"app*/json*", "*/javascript*" })
    public static class Animal {

        private String name;
        private Integer age;

        public Animal() {
        }

        public Animal(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Animal)) {
                return false;
            }

            final Animal animal = (Animal) o;

            if (age != null ? !age.equals(animal.age) : animal.age != null) {
                return false;
            }
            if (name != null ? !name.equals(animal.name) : animal.name != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (age != null ? age.hashCode() : 0);
            return result;
        }
    }
}
