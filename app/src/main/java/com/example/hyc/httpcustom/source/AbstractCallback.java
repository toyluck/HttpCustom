package com.example.hyc.httpcustom.source;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hyc on 2016/11/9.
 */

public abstract class AbstractCallback<T> implements ICallback<T> {

    private volatile AtomicBoolean _canceld = new AtomicBoolean(false);

    @Override
    public void cancelReq() {
        _canceld.set(true);
    }

    private void cancelCheck() throws AppException {
        if (_canceld.getAndSet(false))
            throw new AppException(AppException.ExceptionType.CANCELD, "Request has been canceld");
    }

    private String _path;
    protected Handler _handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    _progressListener.onProgress(msg.arg1, msg.arg2);
                    break;
            }
        }
    };
    private ProgressListener _progressListener;

    public void setPath(String path) {
        _path = path;
    }

    @Override
    @WorkerThread
    public T parse(HttpURLConnection connection) throws AppException {

        try {

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return listRequest(connection);
            } else {
                InputStream           errorStream = connection.getErrorStream();
                byte[]                buf         = new byte[2048];
                int                   len;
                ByteArrayOutputStream bao         = new ByteArrayOutputStream();
                while ((len = errorStream.read(buf)) != -1) {
                    bao.write(buf, 0, len);
                    bao.flush();
                }
                bao.close();
                throw new AppException(AppException.ExceptionType.SERVECE, connection.getResponseCode(),
                        connection.getResponseMessage() + "" +
                                " " +
                                "~|~ " + bao.toString() + " ~|~ ");
            }
        } catch (Exception e) {
            if (e instanceof InterruptedIOException) {
                throw new AppException(AppException.ExceptionType.RES_TIMEOUT, e);
            } else {
                throw new AppException(AppException.ExceptionType.OPERATION, e.getMessage() + " ~|~ " + e
                        .getLocalizedMessage() + " ~|~ " + e
                        .getCause() + " ~|~ ");
            }

        }
    }

    @WorkerThread
    private T listRequest(HttpURLConnection connection) throws IOException, JSONException, AppException {

        //在这个位置进行判断
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        OutputStream        os;
        String              rawResponse;
        if (TextUtils.isEmpty(_path)) {
            os = new ByteArrayOutputStream();
        } else {
            os = new FileOutputStream(_path);
        }
        byte[] buf = new byte[1024];
        int    len;
        _progressListener = getProgressListener();
        int count      = 0;
        int totalCount = connection.getContentLength();
        while ((len = inputStream.read(buf)) != -1) {
            cancelCheck();
            if (_progressListener != null) {
                count += len;
                _handler.obtainMessage(1, count, totalCount).sendToTarget();

            }

            os.write(buf, 0, len);
            os.flush();
        }
        inputStream.close();
        os.close();
        if (TextUtils.isEmpty(_path)) {
            rawResponse = os.toString();
        } else {
            rawResponse = _path;
        }
        T t = bindData(rawResponse);
        postRequest(t);
        return t;
    }

    @Override
    public void postRequest(T t) {
    }

    @Override
    public T checkAndGetDataFromCache() {

        return null;
    }

    @Override
    public void onFromCache(T t) {

    }

    /**
     * 检查实现类是否实现了 {@link ProgressListener}
     *
     * @return 实现了该接口的实现类
     */
    @Nullable
    private ProgressListener getProgressListener() {

        Class<?>[]       interfaces       = this.getClass().getSuperclass().getInterfaces();
        ProgressListener progressListener = null;
        for (Class<?> anInterface : interfaces) {
            if (anInterface == ProgressListener.class) {
                progressListener = (ProgressListener) this;
                break;
            }
        }
        return progressListener;
    }

    protected abstract T bindData(String rawResponse) throws JSONException, AppException;

}
