package com.example.hyc.httpcustom.source;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.hyc.httpcustom.utils.esprossoUtils.EsprossoIdelResource;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hyc on 16-11-9.
 * 执行线程操作
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

        private static final int FROMCACHE = 0x33;
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
                        AppException exception = (AppException) msg.obj;
                        _request.globalCatch(exception);

                        break;
                    case FROMCACHE:
                        _request._iCallback.onFromCache(msg.obj);
                        break;
                }
            }
        };

        DefaultTask(Request request) {
            _request = request;
        }

        @Override
        public void run() {
            request();
        }

        private AtomicInteger _retryCount = new AtomicInteger(0);

        private void request() {
            try {
                EsprossoIdelResource.increment();

                _request.checkCanceld();

                final HttpURLConnection conn = HttpConnectUtil.execute(_request);

                _request.checkCanceld();

                //先检查数据库后 再去网络请求
                Object fromCache = _request._iCallback.checkAndGetDataFromCache();
                if (fromCache != null) {
                    _handler.obtainMessage(FROMCACHE, fromCache);
                }

                Object obj = _request._iCallback.parse(conn);

                _handler.obtainMessage(SUCCESED, obj).sendToTarget();

            } catch (final AppException e) {
                switch (e.getExceptionType()) {
                    case REQ_TIMEOUT:
                    case RES_TIMEOUT:
                        //超时操作都要进行重新请求
                        if (_retryCount.getAndIncrement() <= _request.getMaxRetryCount()) {
                            Log.d(TAG, "request: has retry for" + _retryCount.get());
                            request();
                        }
                        return;
                    default:

                }
                _handler.obtainMessage(FAILUED, e).sendToTarget();
                e.printStackTrace();
            } finally {

                EsprossoIdelResource.decrement();

            }
        }
    }

    private static final String TAG = "RequestTask";
}
