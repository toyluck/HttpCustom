package com.example.hyc.httpcustom.utils.esprossoUtils;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hyc on 2016/11/9.
 */

class SampleIdleResource implements IdlingResource {

    private final String           _name;
    private       ResourceCallback _callback;

    SampleIdleResource(String name) {
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }

    private AtomicInteger _count = new AtomicInteger(0);

    @Override
    public boolean isIdleNow() {
        return _count.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        _callback = callback;
    }


    void increment() {
        _count.getAndIncrement();
    }

    void decrement() {
        int i = _count.decrementAndGet();
        if (i == 0) {
            if (_callback != null) {
                _callback.onTransitionToIdle();
            }
        } else if (i < 0) {
            throw new UnknownError("_count can't be less than 0");
        }

    }
}
