package com.example.hyc.httpcustom.source;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
        static final int SUCCESED = 0x11;
        static final int FAILUED  = 0x22;
        Handler _handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESED:
                        _request._iCallback.onSuccessed(msg.obj);

                        break;
                    case FAILUED:
                        // 处理一些全局的操作
                        _request.globalCatch((AppException) msg.obj);

                        break;
                }
            }
        };

        DefaultTask(Request request) {
            _request = request;
        }

        @Override
        public void run() {
            try {
                final HttpURLConnection conn = HttpConnectUtil.execute(_request);
                _handler.obtainMessage(SUCCESED, _request._iCallback.parse(conn));
                EsprossoIdelResource.decrement();
            } catch (final AppException e) {
                EsprossoIdelResource.decrement();
                _handler.obtainMessage(FAILUED, e);
                e.printStackTrace();
            }
        }
    }
}
