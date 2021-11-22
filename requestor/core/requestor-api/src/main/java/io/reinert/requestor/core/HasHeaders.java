package io.reinert.requestor.core;

import io.reinert.requestor.core.header.Header;

public interface HasHeaders {
    Headers getHeaders();

    String getHeader(String headerName);

    boolean hasHeader(String headerName);

    /**
     * Adds a header overwriting existing headers with the same name.
     *
     * @param header    The header to be inserted
     */
    void setHeader(Header header);

    void setHeader(String headerName, String headerValue);

    /**
     * Removes a header and returns the removed header.
     *
     * @param headerName    The name of the header
     * @return  The removed header instance, if it exists
     */
    Header delHeader(String headerName);
}
