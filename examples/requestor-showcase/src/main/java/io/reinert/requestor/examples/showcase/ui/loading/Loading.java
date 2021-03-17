package io.reinert.requestor.examples.showcase.ui.loading;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.examples.showcase.ui.loading.event.HideLoadingEvent;
import io.reinert.requestor.examples.showcase.ui.loading.event.ShowLoadingEvent;

public class Loading extends Composite implements ShowLoadingEvent.Handler, HideLoadingEvent.Handler {
    interface LoadingUiBinder extends UiBinder<HTMLPanel, Loading> { }

    private static LoadingUiBinder ourUiBinder = GWT.create(LoadingUiBinder.class);

    private Map<Object, Integer> activeRequestsMap = new HashMap<Object, Integer>();

    @UiField
    HTMLPanel messageWrapper;

    public Loading(EventBus eventBus) {
        initWidget(ourUiBinder.createAndBindUi(this));
        setVisible(false);
        eventBus.addHandler(ShowLoadingEvent.TYPE, this);
        eventBus.addHandler(HideLoadingEvent.TYPE, this);
    }

    @Override
    public void onHideLoading(HideLoadingEvent event) {
        final Integer activeRequests = activeRequestsMap.get(event.getSource());
        if (activeRequests != null) {
            if (activeRequests == 1) {
                activeRequestsMap.remove(event.getSource());
            } else {
                activeRequestsMap.put(event.getSource(), activeRequests - 1);
            }
            if (activeRequestsMap.isEmpty()) setVisible(false);
        }
    }

    @Override
    public void onShowLoading(ShowLoadingEvent event) {
        final Integer activeRequests = activeRequestsMap.get(event.getSource());
        if (activeRequests == null) {
            activeRequestsMap.put(event.getSource(), 1);
        } else {
            activeRequestsMap.put(event.getSource(), activeRequests + 1);
        }
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        messageWrapper.setVisible(visible);
    }
}
