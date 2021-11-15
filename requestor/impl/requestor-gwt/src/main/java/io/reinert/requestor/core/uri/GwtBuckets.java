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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

final class GwtBuckets implements Buckets {

    static {
        Factory.INSTANCE = new Factory() {
            @Override
            public Buckets create() {
                return new GwtBuckets();
            }
        };
    }

    private final JsBuckets delegate = JavaScriptObject.createObject().cast();

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
    public Buckets copy() {
        return delegate.copyNative();
    }

    @Override
    public String[] getKeys() {
        return delegate.getKeys();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    private static final class JsBuckets extends JavaScriptObject implements Buckets {

        protected JsBuckets() {
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
        public String[] get(String key) {
            return toStringArray(getNative(key));
        }

        @Override
        public String[] remove(String key) {
            return toStringArray(removeNative(key));
        }

        @Override
        public Buckets copy() {
            return copyNative();
        }

        @Override
        public String[] getKeys() {
            return toStringArray(getKeysNative());
        };

        @Override
        public boolean isEmpty() {
            return getKeysNative().length() == 0;
        }

        public boolean isEquals(JsBuckets other) {
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

        public native String stringify() /*-{
            return JSON.stringify(this);
        }-*/;

        private native JsArrayString getNative(String key) /*-{
            return this[key];
        }-*/;

        private native JsArrayString getKeysNative() /*-{
            return Object.keys(this);
        }-*/;

        private native JsArrayString removeNative(String key) /*-{
            var bucket = this[key];
            delete this[key];
            return bucket;
        }-*/;

        private native JsBuckets copyNative() /*-{
            var copy, key, i;
            copy = {};
            for (key in this) {
                copy[key] = [];
                i = this[key].length;
                while (i--) copy[key][i] = this[key][i];
            }
            return copy;
        }-*/;

        private static String[] toStringArray(JsArrayString jsArray) {
            if (GWT.isScript()) {
                return reinterpretCast(jsArray);
            } else {
                int length = jsArray.length();
                String[] ret = new String[length];
                for (int i = 0; i < length; i++) {
                    ret[i] = jsArray.get(i);
                }
                return ret;
            }
        }

        private static native <T> T[] reinterpretCast(JsArrayString jsArray) /*-{
            return jsArray;
        }-*/;

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
