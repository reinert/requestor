package io.reinert.requestor;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A container of {@link Provider}.
 *
 * @author Danilo Reinert
 */
public interface ProviderManager {

    /**
     * Register a {@link Provider}.
     *
     * @param provider  the provider to register
     *
     * @return  the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    HandlerRegistration register(Provider<?> provider);
}
