package io.reinert.requestor;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class RequestInProgress extends com.google.gwt.http.client.Request {

    /**
     * Constructs an instance of the Request object.
     *
     * @param xmlHttpRequest JavaScript XmlHttpRequest object instance
     * @param timeoutMillis  number of milliseconds to wait for a response
     * @param callback       callback interface to use for notification
     *
     * @throws IllegalArgumentException if timeoutMillis &lt; 0
     * @throws NullPointerException     if xmlHttpRequest, or callback are null
     */
    public RequestInProgress(XMLHttpRequest xmlHttpRequest, int timeoutMillis, RequestCallback callback) {
        super(xmlHttpRequest, timeoutMillis, callback);
    }
}
