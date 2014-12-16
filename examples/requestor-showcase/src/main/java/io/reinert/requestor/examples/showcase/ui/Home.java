package io.reinert.requestor.examples.showcase.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Home extends Composite {

    public interface Handler {
    }

    interface HomeUiBinder extends UiBinder<Widget, Home> {}

    private static HomeUiBinder ourUiBinder = GWT.create(HomeUiBinder.class);

    @UiField
    CellTree mainMenu;

    private Handler handler;

    public Home() {
        Widget rootElement = ourUiBinder.createAndBindUi(this);

        initWidget(rootElement);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}