/*
 * Copyright 2014 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.examples.showcase.activity;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.core.RequestProgress;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ProgressCallback;
import io.reinert.requestor.examples.showcase.ui.BinaryData;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.gwt.payload.SerializedJsPayload;
import io.reinert.requestor.gwt.type.Blob;

public class BinaryDataActivity extends ShowcaseActivity implements BinaryData.Handler {

    private final BinaryData view;
    private final Session session;

    public BinaryDataActivity(String section, BinaryData view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void onSendButtonClick(JavaScriptObject file) {
        view.setSendProgressStatus(0);
        view.setSendText(null);
        session.req("https://httpbin.org/post")
                .payload(SerializedJsPayload.fromBlob(file))
                .post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setSendText(result);
                    }
                })
                .onUpProgress(new ProgressCallback() {
                    @Override
                    public void execute(RequestProgress progress) {
                        if (progress.isLengthComputable())
                            view.setSendProgressStatus(progress.getCompletedFraction(100));
                    }
                })
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setSendProgressStatus(100);
                    }
                });
    }

    @Override
    public void onRetrieveButtonClick(String url) {
        session.req(url)
                .get(Blob.class)
                .onProgress(new ProgressCallback() {
                    @Override
                    public void execute(RequestProgress progress) {
                        if (progress.isLengthComputable())
                            view.setRetrieveProgressStatus(progress.getCompletedFraction(100));
                    }
                })
                .onSuccess(new PayloadCallback<Blob>() {
                    @Override
                    public void execute(Blob result) {
                        view.setRetrieveProgressStatus(100);

                        final JavaScriptObject blob = result.asJso();
                        if (blob == null) {
                            Window.alert("No content received.");
                            view.setRetrieveProgressStatus(0);
                        }

                        appendImage(blob, Document.get().getElementById("img-container"));
                    }
                });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Binary Data");
        Page.setDescription("Easily transfer binary data from/to server.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    private native void appendImage(JavaScriptObject blob, Element container) /*-{
        container.innerHTML = "";
        var img = $doc.createElement('img');
        img.onload = function() {
            $wnd.URL.revokeObjectURL(img.src); // Clean up after yourself.
        };
        img.className = 'img-responsive img-thumbnail';
        img.src = $wnd.URL.createObjectURL(blob);
        container.appendChild(img);
    }-*/;
}
