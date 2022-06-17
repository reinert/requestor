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
package io.reinert.requestor.java.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reinert.requestor.core.Base64Codec;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestorCore;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.auth.DigestAuth;
import io.reinert.requestor.core.deferred.DeferredPoolFactoryImpl;
import io.reinert.requestor.core.uri.UriBuilder;
import io.reinert.requestor.java.serialization.BinarySerializer;
import io.reinert.requestor.java.serialization.ByteSerializer;
import io.reinert.requestor.java.serialization.FileSerializer;
import io.reinert.requestor.java.serialization.FormDataMultiPartSerializer;
import io.reinert.requestor.java.serialization.InputStreamSerializer;

/**
 * This class provides a static initializer for Requestor's deferred bindings for JVM environment.
 *
 * @author Danilo Reinert
 */
public class Requestor {

    static {
        init();
    }

    public static final String CHUNKED_STREAMING_MODE_DISABLED = "requestor.java.net.chunkedStreamingModeDisabled";
    public static final String DEFAULT_CONTENT_TYPE = "requestor.java.net.defaultContentType";
    public static final String READ_CHUNKING_ENABLED = "requestor.java.net.readChunkingEnabled";
    public static final String WRITE_CHUNKING_ENABLED = "requestor.java.net.writeChunkingEnabled";
    public static final String INPUT_BUFFER_SIZE = "requestor.java.net.inputBufferSize";
    public static final String OUTPUT_BUFFER_SIZE = "requestor.java.net.outputBufferSize";

    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static boolean initialized = false;

    public static Session newSession() {
        return newSession(new DeferredPoolFactoryImpl());
    }

    public static Session newSession(ScheduledExecutorService scheduledExecutorService) {
        return newSession(new DeferredPoolFactoryImpl(), new JavaNetRequestDispatcherFactory(scheduledExecutorService));
    }

    public static Session newSession(DeferredPool.Factory deferredPoolFactory) {
        return newSession(deferredPoolFactory, Executors.newScheduledThreadPool(DEFAULT_CORE_POOL_SIZE));
    }

    public static Session newSession(DeferredPool.Factory deferredPoolFactory,
                                     ScheduledExecutorService scheduledExecutorService) {
        return newSession(deferredPoolFactory, new JavaNetRequestDispatcherFactory(scheduledExecutorService));
    }

    public static Session newSession(DeferredPool.Factory deferredPoolFactory,
                                     RequestDispatcher.Factory requestDispatcherFactory) {
        return configure(new Session(requestDispatcherFactory, deferredPoolFactory));
    }

    public static UriBuilder newUriBuilder() {
        return UriBuilder.newInstance();
    }

    private static Session configure(Session session) {
        RequestorCore.configure(session);

        session.save(Requestor.DEFAULT_CONTENT_TYPE, "text/plain");

        session.register(BinarySerializer.getInstance());
        session.register(ByteSerializer.getInstance());
        session.register(FileSerializer.getInstance());
        session.register(FormDataMultiPartSerializer.getInstance());
        session.register(InputStreamSerializer.getInstance());

        return session;
    }

    /**
     * Initializes static lazy bindings to proper usage of Requestor in JVM environment.
     * <p></p>
     * Call this method in a static block in the app's EntryPoint.
     */
    private static void init() {
        if (!RequestorCore.isInitialized()) {
            RequestorCore.init(
                    new Base64Codec() {
                        public String decode(String encoded, String charset) {
                            try {
                                return new String(Base64.getDecoder().decode(encoded), charset);
                            } catch (UnsupportedEncodingException e) {
                                throw new UnsupportedOperationException(
                                        "Cannot decode base64 input to charset '" + charset + "'.", e);
                            }
                        }

                        public String decode(byte[] encoded, String charset) {
                            try {
                                return new String(Base64.getDecoder().decode(encoded), charset);
                            } catch (UnsupportedEncodingException e) {
                                throw new UnsupportedOperationException(
                                        "Cannot decode base64 input to charset '" + charset + "'.", e);
                            }
                        }

                        public String encode(String text, String charset) {
                            try {
                                return Base64.getEncoder().encodeToString(text.getBytes(charset));
                            } catch (UnsupportedEncodingException e) {
                                throw new UnsupportedOperationException(
                                        "Cannot encode base64 input from charset '" + charset + "'.", e);
                            }
                        }
                    },
                    new JavaNetUriCodec()
            );
        }

        if (!initialized) {
            if (!DigestAuth.hasHashFunction("md5")) {
                try {
                    MessageDigest.getInstance("MD5");
                    DigestAuth.setHashFunction("md5", new DigestAuth.HashFunction() {
                        public String hash(String input, String charset) {
                            MessageDigest md = null;
                            try {
                                md = MessageDigest.getInstance("MD5");
                            } catch (NoSuchAlgorithmException e) {
                                throw new UnsupportedOperationException(
                                        "Cannot perform MD5 hashing because MD5 MessageDigest is not available.", e);
                            }
                            try {
                                md.update(input.getBytes(charset));
                            } catch (UnsupportedEncodingException e) {
                                throw new UnsupportedOperationException("Cannot hash to charset '" + charset + "'.", e);
                            }
                            byte[] digest = md.digest();
                            return bytesToHex(digest);
                        }
                    });
                } catch (NoSuchAlgorithmException ignored) { }
            }

            allowPatchMethodOnHttpUrlConnection();

            initialized = true;
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
        } catch (Exception ignored) {
            allowPatchWithUnsafe();
        }
    }

    private static void allowPatchWithUnsafe() {
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            final Object unsafe = theUnsafe.get(null);

            final Field methods = HttpURLConnection.class.getDeclaredField("methods");
            methods.setAccessible(true);

            final Method staticFieldBase = unsafeClass.getMethod("staticFieldBase", Field.class);
            final Method staticFieldOffset = unsafeClass.getMethod("staticFieldOffset", Field.class);

            final Object fieldBase = staticFieldBase.invoke(unsafe, methods);
            final long fieldOffset = (long) staticFieldOffset.invoke(unsafe, methods);

            final Method putObject = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);

            // Calling get is necessary to actually update the field value in the next statement
            methods.get(HttpURLConnection.class);
            putObject.invoke(unsafe, fieldBase, fieldOffset, new String[] {
                    "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"
            });

            methods.setAccessible(false);
            theUnsafe.setAccessible(false);
        } catch (Exception ignored) { }
    }
}
