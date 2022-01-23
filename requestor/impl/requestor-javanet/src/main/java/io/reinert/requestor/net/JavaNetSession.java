package io.reinert.requestor.net;

import io.reinert.requestor.core.*;
import io.reinert.requestor.core.deferred.DeferredPoolFactoryImpl;
import io.reinert.requestor.net.dispatcher.DispatcherFactory;
import io.reinert.requestor.net.serialization.RequestSerializer;
import io.reinert.requestor.net.serialization.ResponseDeserializer;

public class JavaNetSession extends Session {

    public JavaNetSession() {
        this(new DeferredPoolFactoryImpl());
    }


    public JavaNetSession(DeferredPool.Factory deferredPoolFactory) {
        this(deferredPoolFactory,
                RequestSerializer.getInstance(),
                ResponseDeserializer.getInstance());
    }

    public JavaNetSession(DeferredPool.Factory deferredPoolFactory, RequestSerializer requestSerializer,
                          ResponseDeserializer responseDeserializer) {
        super(new DispatcherFactory(), deferredPoolFactory, requestSerializer, responseDeserializer);
    }

    @Override
    protected void configure() {
        super.configure();
        // TODO
    }
}
