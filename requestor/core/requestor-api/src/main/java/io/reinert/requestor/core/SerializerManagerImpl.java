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
package io.reinert.requestor.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.requestor.core.serialization.Deserializer;
import io.reinert.requestor.core.serialization.HandlesSubTypes;
import io.reinert.requestor.core.serialization.SerializationException;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * Manager for registering and retrieving Serializers and Deserializers.
 *
 * @author Danilo Reinert
 */
class SerializerManagerImpl implements SerializerManager {

    private static final Logger logger = Logger.getLogger(SerializerManagerImpl.class.getName());

    private final Map<String, ArrayList<DeserializerHolder>> deserializers = new HashMap<String,
            ArrayList<DeserializerHolder>>();
    private final Map<String, ArrayList<SerializerHolder>> serializers = new HashMap<String,
            ArrayList<SerializerHolder>>();

    /**
     * Register a deserializer of the given type.
     *
     * @param deserializer  The deserializer.
     *
     * @return  The {@link Registration} object, capable of cancelling this Registration to the
     *          {@link SerializerManagerImpl}.
     */
    public Registration register(final Deserializer<?> deserializer) {
        return register(new DeserializerProvider() {
            @Override
            public Deserializer<?> getInstance() {
                return deserializer;
            }
        });
    }

    @Override
    public Registration register(DeserializerProvider deserializer) {
        // Register deserializer only
        return registerDeserializer(deserializer);
    }

    /**
     * Register a serializer of the given type.
     *
     * @param serializer  The serializer.
     *
     * @return  The {@link Registration} object, capable of cancelling this Registration
     *          to the {@link SerializerManagerImpl}.
     */
    public Registration register(final Serializer<?> serializer) {
        return register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return serializer;
            }
        });
    }

    @Override
    public Registration register(SerializerProvider serializerProvider) {
        // Register both serializer and deserializer
        final Registration desReg = registerDeserializer(serializerProvider);
        final Registration serReg = registerSerializer(serializerProvider);

        return new Registration() {
            public void cancel() {
                desReg.cancel();
                serReg.cancel();
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
    public <T> Deserializer<T> getDeserializer(Class<T> type, String mediaType) throws SerializationException {
        checkNotNull(type, "Type (Class<T>) cannot be null.");
        checkNotNull(mediaType, "Media-Type string cannot be null.");

        final String typeName = getClassName(type);
        final Key key = new Key(typeName, mediaType);

        logger.log(Level.FINE, "Querying for Deserializer of type '" + typeName + "' and " + "media-type '" + mediaType
                + "'.");

        ArrayList<DeserializerHolder> holders = deserializers.get(typeName);
        if (holders != null) {
            for (DeserializerHolder holder : holders) {
                if (holder.key.matches(key)) {
                    Deserializer<T> deserializer = (Deserializer<T>) holder.deserializerProvider.getInstance();
                    logger.log(Level.FINE, "Deserializer for type '" + deserializer.handledType() + "' and " +
                            "media-type '" + Arrays.toString(deserializer.mediaType()) + "' matched: " +
                            deserializer.getClass().getName());
                    return deserializer;
                }
            }
        }

        logger.log(Level.WARNING, "There is no Deserializer registered for " + type.getName() +
                " and media-type " + mediaType + ".");

        return null;
    }

    /**
     * Retrieve Serializer from manager.
     *
     * @param type The type class of the serializer.
     * @param <T> The type of the serializer.
     * @return The serializer of the specified type.
     *
     * @throws SerializationException if no serializer was registered for the class.
     */
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getSerializer(Class<T> type, String mediaType) throws SerializationException {
        checkNotNull(type, "Type (Class<T>) cannot be null.");
        checkNotNull(mediaType, "Media-Type string cannot be null.");

        final String typeName = getClassName(type);
        final Key key = new Key(typeName, mediaType);

        logger.log(Level.FINE, "Querying for Serializer of type '" + typeName + "' and " + "media-type '" + mediaType
                + "'.");

        ArrayList<SerializerHolder> holders = serializers.get(typeName);
        if (holders != null) {
            for (SerializerHolder holder : holders) {
                if (holder.key.matches(key)) {
                    Serializer<T> serializer = (Serializer<T>) holder.serializerProvider.getInstance();
                    logger.log(Level.FINE, "Serializer for type '" + serializer.handledType() + "' and " +
                            "media-type '" + Arrays.toString(serializer.mediaType()) + "' matched: " +
                            serializer.getClass().getName());
                    return serializer;
                }
            }
        }

        logger.log(Level.WARNING, "There is no Serializer registered for type " + type.getName() +
                " and media-type " + mediaType + ".");

        return null;
    }

    private <T> String getClassName(Class<T> type) {
        // We don't use getCanonicalName because GWT AutoBean returns null for it
        return type.getName();
    }

    private Registration bindSerializerToType(SerializerProvider serializerProvider,
                                              Class<?> type, String[] mediaType) {
        final String typeName = getClassName(type);
        ArrayList<SerializerHolder> allHolders = serializers.get(typeName);
        if (allHolders == null) {
            allHolders = new ArrayList<SerializerHolder>();
            serializers.put(typeName, allHolders);
        }

        final SerializerHolder[] currHolders = new SerializerHolder[mediaType.length];
        for (int i = 0; i < mediaType.length; i++) {
            String pattern = mediaType[i];
            final Key key = new Key(typeName, pattern);
            final SerializerHolder holder = new SerializerHolder(key, serializerProvider);
            allHolders.add(holder);
            currHolders[i] = holder;
        }

        Collections.sort(allHolders);

        return new Registration() {
            @Override
            public void cancel() {
                for (SerializerHolder holder : currHolders) {
                    serializers.get(typeName).remove(holder);
                }
            }
        };
    }

    private Registration bindDeserializerToType(DeserializerProvider deserializerProvider,
                                                Class<?> type, String[] mediaTypes) {
        final String typeName = getClassName(type);
        ArrayList<DeserializerHolder> allHolders = deserializers.get(typeName);
        if (allHolders == null) {
            allHolders = new ArrayList<DeserializerHolder>();
            deserializers.put(typeName, allHolders);
        }

        final DeserializerHolder[] currHolders = new DeserializerHolder[mediaTypes.length];
        for (int i = 0; i < mediaTypes.length; i++) {
            final String pattern = mediaTypes[i];
            final Key key = new Key(typeName, pattern);
            final DeserializerHolder holder = new DeserializerHolder(key, deserializerProvider);
            allHolders.add(holder);
            currHolders[i] = holder;
        }

        Collections.sort(allHolders);

        return new Registration() {
            @Override
            public void cancel() {
                for (DeserializerHolder holder : currHolders) {
                    deserializers.get(typeName).remove(holder);
                }
            }
        };
    }

    private void checkNotNull(Object o, String message) {
        if (o == null) throw new NullPointerException(message);
    }

    private Registration registerDeserializer(DeserializerProvider deserializerProvider) {
        final Deserializer<?> deserializer = deserializerProvider.getInstance();

        final Registration reg = bindDeserializerToType(deserializerProvider,
                deserializer.handledType(), deserializer.mediaType());

        if (deserializer instanceof HandlesSubTypes) {
            @SuppressWarnings("unchecked")
            List<Class<?>> impls = ((HandlesSubTypes) deserializer).handledSubTypes();

            final Registration[] regs = new Registration[impls.size() + 1];
            regs[0] = reg;

            for (int i = 0; i < impls.size(); i++) {
                Class<?> impl = impls.get(i);
                regs[i + 1] = bindDeserializerToType(deserializerProvider, impl, deserializer.mediaType());
            }

            return new Registration() {
                public void cancel() {
                    for (Registration reg : regs) {
                        reg.cancel();
                    }
                }
            };
        }

        return reg;
    }

    private <T> Registration registerSerializer(SerializerProvider serializerProvider) {
        final Serializer<?> serializer = serializerProvider.getInstance();

        final Registration reg = bindSerializerToType(serializerProvider, serializer.handledType(),
                serializer.mediaType());

        if (serializer instanceof HandlesSubTypes) {
            @SuppressWarnings("unchecked")
            List<Class<?>> impls = ((HandlesSubTypes) serializer).handledSubTypes();

            final Registration[] regs = new Registration[impls.size() + 1];
            regs[0] = reg;

            for (int i = 0; i < impls.size(); i++) {
                Class<?> impl = impls.get(i);
                regs[i + 1] = bindSerializerToType(serializerProvider, impl, serializer.mediaType());
            }

            return new Registration() {
                public void cancel() {
                    for (Registration reg : regs) {
                        reg.cancel();
                    }
                }
            };
        }

        return reg;
    }

    private static class DeserializerHolder implements Comparable<DeserializerHolder> {

        final Key key;
        final DeserializerProvider deserializerProvider;

        private DeserializerHolder(Key key, DeserializerProvider deserializerProvider) {
            this.key = key;
            this.deserializerProvider = deserializerProvider;
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
        final SerializerProvider serializerProvider;

        private SerializerHolder(Key key, SerializerProvider serializer) {
            this.key = key;
            this.serializerProvider = serializer;
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

    // TODO: Move to a separate file and package access level class in order to test this unit properly
    private static class Key implements Comparable<Key> {

        final String typeName;
        final String mediaType;
        final double factor;

        private Key(String typeName, String mediaType) {
            checkSeparatorPresence(mediaType);

            this.typeName = typeName;
            this.mediaType = mediaType;
            this.factor = 1.0;
        }

        private Key(String typeName, String mediaType, double factor) {
            this.typeName = typeName;
            this.mediaType = mediaType;
            this.factor = factor;
        }

        public boolean matches(Key key) {
            if (!key.typeName.equals(this.typeName)) {
                return false;
            }

            boolean matches;

            final int thisSep = this.mediaType.indexOf("/");
            final int otherSep = key.mediaType.indexOf("/");

            String thisInitialPart = this.mediaType.substring(0, thisSep);
            String otherInitialPart = key.mediaType.substring(0, otherSep);

            if (thisInitialPart.contains("*")) {
                matches = matchPartsSafely(thisInitialPart, otherInitialPart);
            } else if (otherInitialPart.contains("*")) {
                matches = matchPartsUnsafely(otherInitialPart, thisInitialPart);
            } else {
                matches = thisInitialPart.equalsIgnoreCase(otherInitialPart);
            }

            if (!matches) return false;

            final String thisFinalPart = this.mediaType.substring(thisSep + 1);
            final String otherFinalPart = key.mediaType.substring(otherSep + 1);

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

            if (!typeName.equals(key.typeName)) {
                return false;
            }
            if (!mediaType.equals(key.mediaType)) {
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
            result = typeName.hashCode();
            result = 31 * result + mediaType.hashCode();
            temp = Double.doubleToLongBits(factor);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public int compareTo(Key key) {
            int result = this.typeName.compareTo(key.typeName);

            // TODO: Improve pattern matching to handle patterns without separators?
            if (result == 0) {
                final int thisSep = this.mediaType.indexOf("/");
                final int otherSep = key.mediaType.indexOf("/");

                // !!! CAUTION !!!
                // When mediaType does not have a '/' separator, than StringArrayIndexOutOfBounds is thrown.
                String thisInitialPart = this.mediaType.substring(0, thisSep);
                String otherInitialPart = key.mediaType.substring(0, otherSep);
                result = thisInitialPart.compareTo(otherInitialPart);

                // Invert the result if the winner contains wildcard
                if ((result < 0 && thisInitialPart.contains("*")) || (result > 0 && otherInitialPart.contains("*")))
                    result = -result;

                if (result == 0) {
                    String thisFinalPart = this.mediaType.substring(thisSep + 1);
                    String otherFinalPart = key.mediaType.substring(otherSep + 1);
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

        private void checkSeparatorPresence(String mediaType) {
            if (mediaType.indexOf("/") < 1)
                throw new RuntimeException("Cannot perform matching. Media-Type *" +
                        this.mediaType + "* does not have a '/' separator.");
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
                if (part.length() != 0) {
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
                if (part.length() != 0) {
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
                    "type: '" + typeName + '\'' +
                    ", mediaType: '" + mediaType + '\'' +
                    ", factor: " + factor +
                    '}';
        }
    }
}
