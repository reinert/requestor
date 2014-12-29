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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.ProgressCallback;
import io.reinert.requestor.Payload;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.examples.showcase.ui.BinaryData;
import io.reinert.requestor.examples.showcase.util.Page;

public class BinaryDataActivity extends AbstractActivity implements BinaryData.Handler {

    private final BinaryData view;
    private final Requestor requestor;

    public BinaryDataActivity(BinaryData view, Requestor requestor) {
        this.view = view;
        this.requestor = requestor;
    }

    @Override
    public void onSendButtonClick(JavaScriptObject file) {
        view.setSendProgressStatus(0);
        view.setSendText(null);
        requestor.req("http://httpbin.org/post")
                .payload(new Payload(file))
                .post(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setSendText(result);
                    }
                })
                .upProgress(new ProgressCallback<RequestProgress>() {
                    @Override
                    public void onProgress(RequestProgress progress) {
                        GWT.log("--- UPLOAD PROGRESS ---");
                        GWT.log(progress.isLengthComputable() + "");
                        GWT.log(progress.total() + "");
                        GWT.log(progress.loaded() + "");
                        GWT.log("-----------------------");

                        if (progress.isLengthComputable())
                            view.setSendProgressStatus((progress.loaded() / progress.total()) * 100);
                    }
                })
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setSendProgressStatus(100);
                    }
                });
    }


    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Binary Data");
        Page.setDescription("Easily transfer binary data from/to server.");
        panel.setWidget(view);
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }
}
