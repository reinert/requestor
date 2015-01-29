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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

final class BucketsImpl implements Buckets {

    private final BucketsOverlay delegate = JavaScriptObject.createObject().cast();

    @Override
    public void add(String key, int value) {
        delegate.add(key, value);
    }

    @Override
    public void add(String key, double value) {
        delegate.add(key, value);
    }

    @Override
    public void add(String key, long value) {
        delegate.add(key, value);
    }

    @Override
    public void add(String key, String value) {
        delegate.add(key, value);
    }

    @Override
    public String[] get(String key) {
        return delegate.get(key);
    }

    @Override
    public String[] remove(String key) {
        return delegate.remove(key);
    }

    @Override
    public String[] getKeys() {
        return delegate.getKeys();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    private static final class BucketsOverlay extends JavaScriptObject implements Buckets {

        protected BucketsOverlay() {
        }

        @Override
        public void add(String key, int value) {
            add(key, value + "");
        }

        @Override
        public void add(String key, double value) {
            add(key, value + "");
        }

        @Override
        public void add(String key, long value) {
            add(key, value + "");
        }

        @Override
        public native void add(String key, String value) /*-{
            if (!(key in this)) this[key] = [];
            this[key].push(value);
        }-*/;

        @Override
        public native String[] get(String key) /*-{
            return this[key];
        }-*/;

        @Override
        public native String[] remove(String key) /*-{
            var bucket = this[key];
            delete this[key];
            return bucket;
        }-*/;

        private native JsArrayString getNative(String key) /*-{
            return this[key];
        }-*/;

        @Override
        public native String[] getKeys() /*-{
            return Object.keys(this);
        }-*/;

        private native JsArrayString getKeysNative() /*-{
            return Object.keys(this);
        }-*/;

        @Override
        public boolean isEmpty() {
            return getKeysNative().length() == 0;
        }

        public boolean isEquals(BucketsOverlay other) {
            if (super.equals(other))
                return true;

            final JsArrayString keys = getKeysNative();
            final JsArrayString otherKeys = other.getKeysNative();
            if (arraysEquals(keys, otherKeys)) {
                for (int i = 0; i < keys.length(); i++) {
                    if (!arraysEquals(getNative(keys.get(i)), other.getNative(otherKeys.get(i)))) {
                        return false;
                    }
                }
                return true;
            }

            return false;
        }

        private static native boolean arraysEquals(JsArrayString a, JsArrayString b) /*-{
            if (a === b) return true;
            if (a == null || b == null) return false;
            if (a.length != b.length) return false;

            // clone arrays and sort them
            var a1 = [], b1 = [], l = a1.length;
            for (var i = 0; i < l; ++i) {
                a1[i] = a[i];
                b1[i] = b[i];
            }
            a1.sort();
            b1.sort();

            // compare sorted arrays
            for (var i = 0; i < l; ++i) {
                if (a1[i] !== b1[i]) return false;
            }

            return true;
        }-*/;
    }
}
