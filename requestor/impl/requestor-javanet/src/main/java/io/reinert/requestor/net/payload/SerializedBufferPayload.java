package io.reinert.requestor.net.payload;

import io.reinert.requestor.core.payload.SerializedPayload;

public class SerializedBufferPayload extends SerializedPayload {
    public SerializedBufferPayload(String string) {
        super(string);
    }

    public static SerializedBufferPayload fromString(String payload) {
        return new SerializedBufferPayload(payload);
    }
}
