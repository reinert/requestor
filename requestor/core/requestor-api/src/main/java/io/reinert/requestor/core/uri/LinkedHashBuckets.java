/*
 * Copyright 2015-2021 Danilo Reinert
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
package io.reinert.requestor.core.uri;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Buckets implementation using LinkedHashMap.
 *
 * @author Danilo Reinert
 */
class LinkedHashBuckets implements Buckets {

    private final Map<String, List<String>> bucketsMap = new LinkedHashMap<String, List<String>>();

    @Override
    public void add(String key, int value) {
        getBuckets(key).add(String.valueOf(value));
    }

    @Override
    public void add(String key, double value) {
        getBuckets(key).add(String.valueOf(value));
    }

    @Override
    public void add(String key, long value) {
        getBuckets(key).add(String.valueOf(value));
    }

    @Override
    public void add(String key, String value) {
        getBuckets(key).add(value);
    }

    @Override
    public String[] get(String key) {
        final List<String> buckets = getBuckets(key);
        final String[] keys = new String[buckets.size()];
        return buckets.toArray(keys);
    }

    @Override
    public String[] getKeys() {
        final String[] keys = new String[bucketsMap.size()];
        return bucketsMap.keySet().toArray(keys);
    }

    @Override
    public boolean isEmpty() {
        return bucketsMap.isEmpty();
    }

    @Override
    public String[] remove(String key) {
        final String[] values = get(key);
        bucketsMap.remove(key);
        return values;
    }

    @Override
    public Buckets copy() {
        LinkedHashBuckets copy = new LinkedHashBuckets();
        for (String key : bucketsMap.keySet()) {
            final String[] values = get(key);
            for (String value : values) {
                copy.add(key, value);
            }
        }
        return copy;
    }

    private List<String> getBuckets(String key) {
        List<String> buckets = bucketsMap.get(key);
        if (buckets == null) {
            buckets = new ArrayList<String>();
            bucketsMap.put(key, buckets);
        }
        return buckets;
    }
}
