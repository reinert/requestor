/*
 * Copyright 2021 Danilo Reinert
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
package io.reinert.requestor.gwt;

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.core.FormData;
import io.reinert.requestor.core.FormDataSerializerUrlEncoded;
import io.reinert.requestor.core.RequestSerializer;
import io.reinert.requestor.core.SerializableRequestInProcess;
import io.reinert.requestor.core.SerializationEngine;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.gwt.payload.SerializedJsPayload;

public class GwtRequestSerializer implements RequestSerializer {
    @Override
    public void serialize(SerializableRequestInProcess request, SerializationEngine serializationEngine) {
        Object payload = request.getPayload().asObject();

        if (payload instanceof FormData &&
                !FormDataSerializerUrlEncoded.MEDIA_TYPE.equalsIgnoreCase(request.getContentType())) {
            request.serializePayload(getFormDataSerializedPayload((FormData) payload));
        } else {
            serializationEngine.serializeRequest(request);
        }

        request.proceed();
    }

    private SerializedPayload getFormDataSerializedPayload(FormData formData) {
        if (formData instanceof JsFormData) {
            JsFormData jsFormData = (JsFormData) formData;
            if (jsFormData.getFormElement() != null)
                return SerializedJsPayload.fromFormData(FormDataOverlay.create(jsFormData.getFormElement()));
        }

        FormDataOverlay overlay = FormDataOverlay.create();
        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                overlay.append(param.getName(), (String) value);
            } else {
                overlay.append(param.getName(), (JavaScriptObject) value, param.getFileName());
            }
        }
        return SerializedJsPayload.fromFormData(overlay);
    }
}
