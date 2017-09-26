package com.kinglloy.album.domain.interactor;

import io.reactivex.observers.DisposableObserver;

/**
 * @author jinyalin
 * @since 2017/4/18.
 */

public class DefaultObserver<T> extends DisposableObserver<T> {
    @Override
    public void onNext(T needDownload) {
        // no-op by default.
    }

    @Override
    public void onComplete() {
        // no-op by default.
    }

    @Override
    public void onError(Throwable exception) {
        // no-op by default.
    }
}
