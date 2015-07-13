package io.reinert.requestor;

import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;

/**
 * A container of {@link Serializer}, {@link Deserializer} and {@link Serdes}.
 *
 * @author Danilo Reinert
 */
public interface SerdesManager {

    /**
     * Register a {@link Deserializer}.
     *
     * @param deserializer the deserializer to register
     *
     * @return the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    <T> HandlerRegistration register(Deserializer<T> deserializer);

    /**
     * Register a {@link Serializer}.
     *
     * @param serializer the serializer to register
     *
     * @return the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    <T> HandlerRegistration register(Serializer<T> serializer);

    /**
     * Register a {@link Serdes}.
     *
     * @param serdes the serdes to register
     *
     * @return the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    <T> HandlerRegistration register(Serdes<T> serdes);
}
