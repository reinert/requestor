/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.gson;

import com.google.gson.Gson;

import io.reinert.requestor.core.TypeProvider;

/**
 * Singleton lazy provider for Gson class.
 *
 * @author Danilo Reinert
 */
public class GsonSingletonProvider implements TypeProvider<Gson> {

    private static class GsonHolder {
        static final Gson INSTANCE = new Gson();
    }

    private static class ProviderHolder {
        static final GsonSingletonProvider INSTANCE = new GsonSingletonProvider();
    }

    public static Gson getGson() {
        return GsonHolder.INSTANCE;
    }

    public static GsonSingletonProvider getProvider() {
        return ProviderHolder.INSTANCE;
    }

    @Override
    public Gson getInstance() {
        return GsonHolder.INSTANCE;
    }

    @Override
    public Class<Gson> getType() {
        return Gson.class;
    }
}
