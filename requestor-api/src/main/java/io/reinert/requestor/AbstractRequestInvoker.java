package io.reinert.requestor;

import java.util.Collection;

import io.reinert.requestor.deferred.RequestPromise;
import io.reinert.requestor.header.Header;

public abstract class AbstractRequestInvoker extends RequestBuilderImpl implements RequestInvoker {

    protected final RequestDispatcher dispatcher;
    protected final RequestProcessor processor;

    public AbstractRequestInvoker(String url, RequestDispatcher dispatcher, RequestProcessor processor) {
        super(url);
        this.dispatcher = dispatcher;
        this.processor = processor;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestInvoker accept(String mediaType) {
        super.accept(mediaType);
        return this;
    }

    @Override
    public RequestInvoker contentType(String mediaType) {
        super.contentType(mediaType);
        return this;
    }

    @Override
    public RequestInvoker header(String header, String value) {
        super.header(header, value);
        return this;
    }

    @Override
    public RequestInvoker header(Header header) {
        super.header(header);
        return this;
    }

    @Override
    public RequestInvoker password(String password) {
        super.password(password);
        return this;
    }

    @Override
    public RequestInvoker payload(Object object) throws IllegalArgumentException {
        super.payload(object);
        return this;
    }

    @Override
    public RequestInvoker timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    @Override
    public RequestInvoker user(String user) {
        super.user(user);
        return this;
    }

    @Override
    public RequestInvoker responseType(ResponseType responseType) {
        super.responseType(responseType);
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    @SuppressWarnings("unchecked")
    protected <T, P extends RequestPromise<T>> P send(String method, Class<T> responseType) {
        setMethod(method);
        return (P) dispatcher.dispatch(processor.process(build()), responseType);
    }

    @SuppressWarnings("unchecked")
    protected <T, C extends Collection, P extends RequestPromise<Collection<T>>> P send(String method,
                                                                                        Class<T> responseType,
                                                                                        Class<C> containerType) {
        setMethod(method);
        return (P) dispatcher.dispatch(processor.process(build()), responseType, containerType);
    }
}
