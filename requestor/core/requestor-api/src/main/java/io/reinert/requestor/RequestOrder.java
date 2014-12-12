package io.reinert.requestor;

public interface RequestOrder extends SerializedRequest {

    boolean isWithCredentials();

    void setWithCredentials(boolean withCredentials);

    void setHeader(String name, String value);

    public void send();

}
