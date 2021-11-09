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
package io.reinert.requestor.gwt.test;

import java.util.Date;

/**
 * Just a POJO.
 *
 * @author Danilo Reinert
 */
public class Person {

    private int id;
    private String name;
    private double weight;
    private Date birthday;

    public Person(int id, String name, double weight, Date birthday) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }

        final Person person = (Person) o;

        if (id != person.id) {
            return false;
        }
        if (Double.compare(person.weight, weight) != 0) {
            return false;
        }
        if (!birthday.equals(person.birthday)) {
            return false;
        }
        if (!name.equals(person.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + birthday.hashCode();
        return result;
    }
}
