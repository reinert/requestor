package io.reinert.requestor;

import java.util.Collection;
import java.util.List;

import io.reinert.gdeferred.DoneCallback;

public abstract class ListDoneCallback<T> implements DoneCallback<Collection<T>> {

    public abstract void onDoneCast(List<T> result);

    @Override
    public void onDone(Collection<T> result) {
        onDoneCast((List<T>) result);
    }
}
