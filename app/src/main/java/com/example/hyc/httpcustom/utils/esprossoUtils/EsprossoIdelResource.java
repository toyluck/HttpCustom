package com.example.hyc.httpcustom.utils.esprossoUtils;

/**
 * Created by hyc on 2016/11/9.
 */

public class EsprossoIdelResource {

    static SampleIdleResource _sampleIdleResource = new SampleIdleResource("EspresssIdelResource");

    public static void increment() {
        _sampleIdleResource.increment();
    }

    public static void decrement() {
        _sampleIdleResource.decrement();
    }

    public static SampleIdleResource get() {
        return _sampleIdleResource;
    }


}
