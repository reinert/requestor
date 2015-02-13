package io.reinert.requestor.examples.showcase.ui.loading.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class HideLoadingEvent extends GwtEvent<HideLoadingEvent.Handler> {

    public interface Handler extends EventHandler {
        void onHideLoading(HideLoadingEvent event);
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    public static Type<Handler> getType() {
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onHideLoading(this);
    }
}
