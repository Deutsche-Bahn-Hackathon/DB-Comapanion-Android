package com.dbhackathon.util;

import rx.Observer;

public class NextObserver<T> implements Observer<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Utils.logException(e);
    }

    @Override
    public void onNext(T t) {

    }
}