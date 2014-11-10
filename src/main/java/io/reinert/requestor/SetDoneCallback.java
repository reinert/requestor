package io.reinert.requestor;

import java.util.Collection;
import java.util.Set;

import io.reinert.gdeferred.DoneCallback;

public abstract class SetDoneCallback<T> implements DoneCallback<Collection<T>> {

    public abstract void onDoneCast(Set<T> result);

    @Override
    public void onDone(Collection<T> result) {
        onDoneCast((Set<T>) result);
    }
}
