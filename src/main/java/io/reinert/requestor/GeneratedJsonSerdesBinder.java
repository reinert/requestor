package io.reinert.requestor;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerdesManager;

class GeneratedJsonSerdesBinder {

    private static GeneratedJsonSerdes generatedJsonSerdes;

    public static void bind(SerdesManager serdesManager, ProviderManager providerManager) {
        if (generatedJsonSerdes == null) {
            generatedJsonSerdes = GWT.create(GeneratedJsonSerdes.class);
        }
        for (Serdes<?> serdes : generatedJsonSerdes.getGeneratedSerdes()) {
            serdesManager.addSerdes(serdes);
        }
        for (GeneratedProvider provider : generatedJsonSerdes.getGeneratedProviders()) {
            providerManager.bind(provider.getType(), provider);
        }
    }
}
