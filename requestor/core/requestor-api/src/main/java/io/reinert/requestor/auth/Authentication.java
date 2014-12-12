package io.reinert.requestor.auth;

import io.reinert.requestor.RequestOrder;

public interface Authentication {
    void authenticate(RequestOrder requestOrder);
}
