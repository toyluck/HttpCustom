package com.example.hyc.httpcustom.source;

import android.os.Handler;
import android.os.Looper;

import com.example.hyc.httpcustom.utils.esprossoUtils.EsprossoIdelResource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyc on 16-11-9.
 */
public class RequestTask {

    private ExecutorService _executors = Executors.newCachedThreadPool();


    public RequestTask() {

    }

    public void addTask(Request request) {
        DefaultTask task = new DefaultTask(request);
        _executors.execute(task);
    }


    private static class DefaultTask implements Runnable {

        private final Request _request;
        private Handler _handler = new Handler(Looper.getMainLooper());

        DefaultTask(Request request) {
            _request = request;
        }

        @Override
        public void run() {
            try {
                final HttpURLConnection conn  = HttpConnectUtil.execute(_request);
                final Object            parse = _request._iCallback.parse(conn);
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        _request._iCallback.onSuccessed(parse);
                        EsprossoIdelResource.decrement();
                    }
                });
            } catch (final AppException e) {
                EsprossoIdelResource.decrement();
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        _request._iCallback.onFailure(e);
                    }
                });
                e.printStackTrace();
            }
        }
    }
}
