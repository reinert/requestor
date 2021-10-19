package io.reinert.requestor;

import io.reinert.requestor.header.Header;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

public class AbstractService implements RequestDefaults {

    private final Requestor requestor;
    private final RequestDefaultsImpl defaults;
    private final Store store;
    private final UriBuilder uriBuilder;

    public AbstractService(Requestor requestor, String resourceUri) {
        this.requestor = requestor;
        this.defaults = RequestDefaultsImpl.copy(requestor.getDefaults());
        this.store = new VolatileStore(requestor.getStore());
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
    }

    @Override
    public void reset() {
        defaults.reset();
    }

    @Override
    public void setMediaType(String mediaType) {
        defaults.setMediaType(mediaType);
    }

    @Override
    public String getMediaType() {
        return defaults.getMediaType();
    }

    @Override
    public void setAuth(Auth auth) {
        defaults.setAuth(auth);
    }

    @Override
    public Auth getAuth() {
        return defaults.getAuth();
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        defaults.setTimeout(timeoutMillis);
    }

    @Override
    public int getTimeout() {
        return defaults.getTimeout();
    }

    @Override
    public void setDelay(int delayMillis) {
        defaults.setDelay(delayMillis);
    }

    @Override
    public int getDelay() {
        return defaults.getDelay();
    }

    @Override
    public void setThrottle(int intervalMillis) {
        defaults.setThrottle(intervalMillis);
    }

    @Override
    public void setThrottle(int intervalMillis, int limit) {
        defaults.setThrottle(intervalMillis, limit);
    }

    @Override
    public int getThrottleInterval() {
        return defaults.getThrottleInterval();
    }

    @Override
    public int getThrottleLimit() {
        return defaults.getThrottleLimit();
    }

    @Override
    public void putHeader(Header header) {
        defaults.putHeader(header);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        defaults.setHeader(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return defaults.getHeaders();
    }

    @Override
    public String getHeader(String headerName) {
        return defaults.getHeader(headerName);
    }

    @Override
    public Header popHeader(String headerName) {
        return defaults.popHeader(headerName);
    }

    public Store getStore() {
        return store;
    }

    protected UriBuilder getUriBuilder() {
        return uriBuilder.clone();
    }

    protected void appendMatrixParamsToUri(UriBuilder reqUriBuilder, Object... params) {
        // Check if params were given to the URI
        if (params != null && params.length > 0) {
            if (params.length % 2 > 0) {
                throw new IllegalArgumentException("It should have an even number of arguments, consisting of " +
                        "key and value pairs which will be appended to the request URI");
            }

            for (int i = 0; i < params.length; i = i + 2) {
                reqUriBuilder.matrixParam(params[i].toString(), params[i + 1]);
            }
        }
    }

    protected void appendQueryParamsToUri(UriBuilder reqUriBuilder, Object... params) {
        // Check if params were given to the URI
        if (params != null && params.length > 0) {
            if (params.length % 2 > 0) {
                throw new IllegalArgumentException("It should have an even number of arguments, consisting of " +
                        "key and value pairs which will be appended to the request URI");
            }

            for (int i = 0; i < params.length; i = i + 2) {
                reqUriBuilder.queryParam(params[i].toString(), params[i + 1]);
            }
        }
    }

    protected RequestInvoker request(Uri uri) {
        final RequestInvoker request = requestor.req(uri);
        defaults.apply(request);
        return request;
    }
}
