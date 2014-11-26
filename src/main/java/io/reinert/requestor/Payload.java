package io.reinert.requestor;

import com.google.gwt.core.client.JavaScriptObject;

public class Payload {

    private String string;
    private JavaScriptObject javaScriptObject;

    public Payload(String string) {
        this.string = string;
    }

    public Payload(JavaScriptObject javaScriptObject) {
        this.javaScriptObject = javaScriptObject;
    }

    public boolean isEmpty() {
        return string == null && javaScriptObject == null;
    }

    public String isString() {
        return string;
    }

    public JavaScriptObject isJavaScriptObject() {
        return javaScriptObject;
    }
}
