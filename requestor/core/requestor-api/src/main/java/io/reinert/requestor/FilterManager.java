package io.reinert.requestor;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A container of {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
public interface FilterManager {

    /**
     * Register a request filter.
     *
     * @param requestFilter  the request filter to be registered
     *
     * @return  the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    HandlerRegistration register(RequestFilter requestFilter);

    /**
     * Register a response filter.
     *
     * @param responseFilter  the response filter to be registered
     *
     * @return  the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    HandlerRegistration register(ResponseFilter responseFilter);
}
