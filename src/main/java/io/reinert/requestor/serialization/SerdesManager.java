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
package io.reinert.requestor.serialization;

import java.util.Collections;

import com.google.web.bindery.event.shared.HandlerRegistration;

import org.turbogwt.core.collections.JsArrayList;
import org.turbogwt.core.collections.JsMap;

/**
 * Manager for registering and retrieving Serializers and Deserializers.
 *
 * @author Danilo Reinert
 */
public class SerdesManager {

    private final JsMap<JsArrayList<DeserializerHolder>> deserializers = JsMap.create();
    private final JsMap<JsArrayList<SerializerHolder>> serializers = JsMap.create();

    /**
     * Register a deserializer of the given type.
     *
     * @param type          The class of the deserializer's type.
     * @param deserializer  The deserializer of T.
     * @param <T>           The type of the object to be deserialized.
     *
     * @return  The {@link com.google.web.bindery.event.shared.HandlerRegistration} object,
     *          capable of cancelling this HandlerRegistration to the {@link SerdesManager}.
     */
    public <T> HandlerRegistration putDeserializer(Class<T> type, Deserializer<T> deserializer) {
        final String typeName = type.getName();
        JsArrayList<DeserializerHolder> tDesList = deserializers.get(typeName);
        if (tDesList == null) {
            tDesList = new JsArrayList<DeserializerHolder>();
            deserializers.put(typeName, tDesList);
        }

        final String[] accept = deserializer.accept();
        final DeserializerHolder[] holders = new DeserializerHolder[accept.length];
        for (int i = 0; i < accept.length; i++) {
            String pattern = accept[i];
            final Key key = new Key(type, pattern);
            final DeserializerHolder holder = new DeserializerHolder(key, deserializer);
            tDesList.add(holder);
            holders[i] = holder;
        }

        Collections.sort(tDesList);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                for (DeserializerHolder holder : holders) {
                    deserializers.get(typeName).remove(holder);
                }
            }
        };
    }

    /**
     * Register a serializer of the given type.
     *
     * @param type          The class of the serializer's type.
     * @param serializer  The serializer of T.
     * @param <T>           The type of the object to be serialized.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this HandlerRegistration
     *          to the {@link SerdesManager}.
     */
    public <T> HandlerRegistration putSerializer(Class<T> type, Serializer<T> serializer) {
        final String typeName = type.getName();
        JsArrayList<SerializerHolder> tSerList = serializers.get(typeName);
        if (tSerList == null) {
            tSerList = new JsArrayList<SerializerHolder>();
            serializers.put(typeName, tSerList);
        }

        final String[] contentType = serializer.contentType();
        final SerializerHolder[] holders = new SerializerHolder[contentType.length];
        for (int i = 0; i < contentType.length; i++) {
            String pattern = contentType[i];
            final Key key = new Key(type, pattern);
            final SerializerHolder holder = new SerializerHolder(key, serializer);
            tSerList.add(holder);
            holders[i] = holder;
        }

        Collections.sort(tSerList);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                for (SerializerHolder holder : holders) {
                    serializers.get(typeName).remove(holder);
                }
            }
        };
    }

    /**
     * Register a serializer/deserializer of the given type.
     *
     * @param type      The class of the serializer/deserializer's type.
     * @param serdes    The serializer/deserializer of T.
     * @param <T>       The type of the object to be serialized/deserialized.
     *
     * @return  The {@link HandlerRegistration} object, capable of cancelling this HandlerRegistration
     *          to the {@link SerdesManager}.
     */
    public <T> HandlerRegistration putSerdes(Class<T> type, Serdes<T> serdes) {
        final HandlerRegistration desReg = putDeserializer(type, serdes);
        final HandlerRegistration serReg = putSerializer(type, serdes);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                desReg.removeHandler();
                serReg.removeHandler();
            }
        };
    }

    /**
     * Retrieve Deserializer from manager.
     *
     * @param type The type class of the deserializer.
     * @param <T> The type of the deserializer.
     * @return The deserializer of the specified type.
     *
     * @throws SerializationException if no deserializer was registered for the class.
     */
    @SuppressWarnings("unchecked")
    public <T> Deserializer<T> getDeserializer(Class<T> type, String contentType) throws SerializationException {
        checkNotNull(type, "Type (Class<T>) cannot be null.");
        checkNotNull(contentType, "Content-Type cannot be null.");

        final Key key = new Key(type, contentType);

        JsArrayList<DeserializerHolder> holders = deserializers.get(type.getName());
        if (holders != null) {
            for (DeserializerHolder holder : holders) {
                if (holder.key.matches(key)) return (Deserializer<T>) holder.deserializer;
            }
        }

        throw new SerializationException("There is no Deserializer registered for " + type.getName() +
                " and content-type " + contentType + ".");
    }

    /**
     * Retrieve Serializer from manager.
     *
     * @param type The type class of the serializer.
     * @param <T> The type of the serializer.
     * @return The serializer of the specified type.
     * @throws SerializationException if no serializer was registered for the class.
     */
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getSerializer(Class<T> type, String contentType) throws SerializationException {
        checkNotNull(type, "Type (Class<T>) cannot be null.");
        checkNotNull(contentType, "Content-Type cannot be null.");

        final Key key = new Key(type, contentType);

        JsArrayList<SerializerHolder> holders = serializers.get(type.getName());
        if (holders != null) {
            for (SerializerHolder holder : holders) {
                if (holder.key.matches(key)) return (Serializer<T>) holder.serializer;
            }
        }

        throw new SerializationException("There is no Serializer registered for type " + type.getName() +
                " and content-type " + contentType + ".");
    }

    private void checkNotNull(Object o, String message) {
        if (o == null) throw new NullPointerException(message);
    }

    private static class DeserializerHolder implements Comparable<DeserializerHolder> {

        final Key key;
        final Deserializer<?> deserializer;

        private DeserializerHolder(Key key, Deserializer<?> deserializer) {
            this.key = key;
            this.deserializer = deserializer;
        }

        @Override
        public int compareTo(DeserializerHolder deserializerHolder) {
            return key.compareTo(deserializerHolder.key);
        }

        @Override
        public boolean equals(Object o) {
            final DeserializerHolder that = (DeserializerHolder) o;
            return key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private static class SerializerHolder implements Comparable<SerializerHolder> {

        final Key key;
        final Serializer<?> serializer;

        private SerializerHolder(Key key, Serializer<?> serializer) {
            this.key = key;
            this.serializer = serializer;
        }

        @Override
        public int compareTo(SerializerHolder serializerHolder) {
            return key.compareTo(serializerHolder.key);
        }

        @Override
        public boolean equals(Object o) {
            final SerializerHolder that = (SerializerHolder) o;
            return key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private static class Key implements Comparable<Key> {

        final Class<?> type;
        final String contentType;
        final double factor;

        private Key(Class<?> type, String contentType) {
            checkSeparatorPresence(contentType);

            this.type = type;
            this.contentType = contentType;
            this.factor = 1.0;
        }

        private Key(Class<?> type, String contentType, double factor) {
            this.type = type;
            this.contentType = contentType;
            this.factor = factor;
        }

        // TODO: test exhaustively
        public boolean matches(Key key) {
            if (!key.type.equals(this.type)) {
                return false;
            }

            boolean matches;

            final int thisSep = this.contentType.indexOf("/");
            final int otherSep = key.contentType.indexOf("/");

            String thisInitialPart = this.contentType.substring(0, thisSep);
            String otherInitialPart = key.contentType.substring(0, otherSep);

            if (thisInitialPart.contains("*")) {
                matches = matchPartsSafely(thisInitialPart, otherInitialPart);
            } else if (otherInitialPart.contains("*")) {
                matches = matchPartsUnsafely(otherInitialPart, thisInitialPart);
            } else {
                matches = thisInitialPart.equalsIgnoreCase(otherInitialPart);
            }

            if (!matches) return false;

            final String thisFinalPart = this.contentType.substring(thisSep + 1);
            final String otherFinalPart = key.contentType.substring(otherSep + 1);

            if (thisFinalPart.contains("*")) {
                matches = matchPartsSafely(thisFinalPart, otherFinalPart);
            } else if (otherFinalPart.contains("*")) {
                matches = matchPartsUnsafely(otherFinalPart, thisFinalPart);
            } else {
                matches = thisFinalPart.equalsIgnoreCase(otherFinalPart);
            }

            return matches;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key)) {
                return false;
            }

            final Key key = (Key) o;

            if (!type.equals(key.type)) {
                return false;
            }
            if (!contentType.equals(key.contentType)) {
                return false;
            }
            if (Double.compare(key.factor, factor) != 0) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = type.hashCode();
            result = 31 * result + contentType.hashCode();
            temp = Double.doubleToLongBits(factor);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public int compareTo(Key key) {
            int result = this.type.getSimpleName().compareTo(key.type.getSimpleName());

            // TODO: Improve pattern matching to handle patterns without separators.
            if (result == 0) {
                final int thisSep = this.contentType.indexOf("/");
                final int otherSep = key.contentType.indexOf("/");

                // !!! CAUTION !!!
                // When contentType does not have a '/' separator, than StringArrayIndexOutOfBounds is thrown.
                String thisInitialPart = this.contentType.substring(0, thisSep);
                String otherInitialPart = key.contentType.substring(0, otherSep);
                result = thisInitialPart.compareTo(otherInitialPart);

                // Invert the result if the winner contains wildcard
                if ((result < 0 && thisInitialPart.contains("*")) || (result > 0 && otherInitialPart.contains("*")))
                    result = -result;

                if (result == 0) {
                    String thisFinalPart = this.contentType.substring(thisSep + 1);
                    String otherFinalPart = key.contentType.substring(otherSep + 1);
                    result = thisFinalPart.compareTo(otherFinalPart);

                    // Invert the result if the winner contains wildcard
                    if ((result < 0 && thisFinalPart.contains("*")) || (result > 0 && otherFinalPart.contains("*")))
                        result = -result;

                    if (result == 0) {
                        // Invert comparison because the greater the factor the greater the precedence.
                        result = Double.compare(key.factor, this.factor);
                    }
                }
            }

            return result;
        }

        private void checkSeparatorPresence(String contentType) {
            if (contentType.indexOf("/") < 1)
                throw new RuntimeException("Cannot perform matching. Content-Type *" +
                        this.contentType + "* does not have a '/' separator.");
        }

        private boolean matchPartsSafely(String left, String right) {
            boolean matches = true;
            final String rightCleaned = right.replace("*", "").toLowerCase();
            String[] parts = left.toLowerCase().split("\\*");
            final boolean otherEndsWithWildcard = right.endsWith("*");
            final int otherCleanedLength = rightCleaned.length();
            int i = 0;
            for (String part : parts) {
                if (i == otherCleanedLength && otherEndsWithWildcard) {
                    break;
                }
                if (!part.isEmpty()) {
                    int newIdx = rightCleaned.indexOf(part, i);
                    if (newIdx == -1) {
                        matches = false;
                        break;
                    }
                    i = newIdx + part.length();
                }
            }
            return matches;
        }

        private boolean matchPartsUnsafely(String left, String right) {
            boolean matches = true;
            String rightLower = right.toLowerCase();
            String[] parts = left.toLowerCase().split("\\*");
            int i = 0;
            for (String part : parts) {
                if (!part.isEmpty()) {
                    int newIdx = rightLower.indexOf(part, i);
                    if (newIdx == -1) {
                        matches = false;
                        break;
                    }
                    i = newIdx + part.length();
                }
            }
            return matches;
        }

        @Override
        public String toString() {
            return "{" +
                    "type: '" + type.getName() + '\'' +
                    ", contentType: '" + contentType + '\'' +
                    ", factor: " + factor +
                    '}';
        }
    }
}
