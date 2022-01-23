package io.reinert.requestor.net.dispatcher;

import io.reinert.requestor.core.*;
import io.reinert.requestor.core.deferred.DeferredPoolFactoryImpl;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JavaDispatcherTest {

    @Test
    public void testPostRequestWithPayload() {
        // Given
        Uri uri = UriBuilder.fromUri("https://httpbin.org/anything")
                .build();
        RequestProcessor requestProcessor = mock(RequestProcessor.class);

        ResponseProcessor responseProcessor = mock(ResponseProcessor.class);
        DeferredPoolFactoryImpl deferredFactory = mock(DeferredPoolFactoryImpl.class);


        PreparedRequest request = mock(PreparedRequest.class);
        when(request.getUri()).thenReturn(uri);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getMutableCopy()).thenReturn((MutableSerializedRequest) request);

        // TODO mock payload

        Deferred<?> deferred = mock(Deferred.class);
        PayloadType payload = mock(PayloadType.class);

        JavaDispatcher dispatcher = new JavaDispatcher(
                requestProcessor, responseProcessor, deferredFactory);

        // When
        dispatcher.send(request, deferred, payload);

        // Then
        verify(responseProcessor, times(1)).process(any());
    }
}