package io.reinert.requestor.examples.showcase.ui.loading.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ShowLoadingEvent extends GwtEvent<ShowLoadingEvent.Handler> {

    public interface Handler extends EventHandler {
        void onShowLoading(ShowLoadingEvent event);
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    public static Type<Handler> getType() {
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onShowLoading(this);
    }
}
