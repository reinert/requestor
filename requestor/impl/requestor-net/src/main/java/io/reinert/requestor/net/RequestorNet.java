/*
 * Copyright 2021-2022 Danilo Reinert
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
package io.reinert.requestor.net;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import io.reinert.requestor.core.Requestor;
import io.reinert.requestor.core.auth.DigestAuth;

import sun.misc.Unsafe;

/**
 * This class provides a static initializer for Requestor's deferred bindings for JVM environment.
 *
 * @author Danilo Reinert
 */
public class RequestorNet {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static boolean PENDING_INIT = true;

    /**
     * Initializes static lazy bindings to proper usage of Requestor in JVM environment.
     * <p></p>
     * Call this method in a static block in the app's EntryPoint.
     */
    public static void init() {
        if (!Requestor.isInitialized()) {
            // TODO: define the charcode
            Requestor.init(
                    text -> Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)),
                    new NetUriCodec()
            );
        }

        if (PENDING_INIT) {
            if (!DigestAuth.hasHashFunction("md5")) {
                try {
                    MessageDigest.getInstance("MD5");
                    DigestAuth.setHashFunction("md5", new DigestAuth.HashFunction() {
                        public String hash(String input) {
                            MessageDigest md = null;
                            try {
                                md = MessageDigest.getInstance("MD5");
                            } catch (NoSuchAlgorithmException e) {
                                throw new UnsupportedOperationException(
                                        "Cannot perform MD5 hashing because MD5 MessageDigest is not available.", e);
                            }
                            md.update(input.getBytes(StandardCharsets.UTF_8));
                            byte[] digest = md.digest();
                            return bytesToHex(digest);
                        }
                    });
                } catch (NoSuchAlgorithmException ignored) { }
            }

            allowPatchMethodOnHttpUrlConnection();

            PENDING_INIT = false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static void allowPatchMethodOnHttpUrlConnection() {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            // NOTE: throws in jdk12+
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);
            methodsField.set(null, new String[] {
                    "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"
            });

            if (!"PATCH".equals(((String[]) methodsField.get(HttpURLConnection.class))[4])) {
                throw new NoSuchFieldException();
            }

            methodsField.setAccessible(false);
            modifiersField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            allowPatchWithUnsafe();
        }
    }

    private static void allowPatchWithUnsafe() {
        try {
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            final Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            methodsField.setAccessible(true);

            final Object fieldBase = unsafe.staticFieldBase(methodsField);
            final long fieldOffset = unsafe.staticFieldOffset(methodsField);

            // Calling get is necessary to actually update the field value in the next statement
            methodsField.get(HttpURLConnection.class);
            unsafe.putObject(fieldBase, fieldOffset, new String[] {
                    "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"
            });

            methodsField.setAccessible(false);
            unsafeField.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException ignored) { }
    }
}
