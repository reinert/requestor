package io.reinert.requestor;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A container of {@link RequestInterceptor} and {@link ResponseInterceptor}.
 *
 * @author Danilo Reinert
 */
public interface InterceptorManager {

    /**
     * Register a request interceptor.
     *
     * @param requestInterceptor the request interceptor to be registered
     *
     * @return  the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    HandlerRegistration register(RequestInterceptor requestInterceptor);

    /**
     * Register a response interceptor.
     *
     * @param responseInterceptor The response interceptor to be registered
     *
     * @return  the {@link HandlerRegistration} object, capable of cancelling this registration
     */
    HandlerRegistration register(ResponseInterceptor responseInterceptor);
}
