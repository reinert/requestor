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
package io.reinert.requestor;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link ProviderManagerImpl}.
 */
public class ProviderManagerImplTest {

    private ProviderManagerImpl manager = new ProviderManagerImpl();

    @Test
    public void wrappedManagerShouldNotBeAffected() {
        final Provider<Collection> collectionProvider = manager.get(Collection.class);
        assert collectionProvider != null;
        final Collection c = collectionProvider.getInstance();
        Assert.assertEquals(c.size(), 0);
        Assert.assertEquals(Collection.class, collectionProvider.getType());
    }
}
