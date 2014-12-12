package io.reinert.requestor;

import io.reinert.requestor.auth.Authentication;

class PassThroughAuthentication implements Authentication {

    private static PassThroughAuthentication INSTANCE = new PassThroughAuthentication();

    private PassThroughAuthentication() {
    }

    public static PassThroughAuthentication getInstance() {
        return INSTANCE;
    }

    @Override
    public void authenticate(RequestOrder requestOrder) {
        requestOrder.send();
    }
}
