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
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by hyc on 2016/11/9.
 */

public abstract class AbstractCallback<T> implements ICallback<T> {

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
                int                   len         = 0;
                ByteArrayOutputStream bao         = new ByteArrayOutputStream();
                while ((len = errorStream.read(buf)) != -1) {
                    bao.write(buf, 0, len);
                    bao.flush();
                }

                bao.close();

                throw new AppException(connection.getResponseCode() , connection.getResponseMessage() + " " +
                        "~|~ " + bao.toString() + " ~|~ ");
            }
        } catch (Exception e) {
            throw new AppException(e.getMessage() + " ~|~ " + e.getLocalizedMessage() + " ~|~ " + e
                    .getCause() + " ~|~ ");

        }
    }

    @WorkerThread
    private T listRequest(HttpURLConnection connection) throws IOException, JSONException {

        //在这个位置进行判断
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        OutputStream        os          = null;
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
        return bindData(rawResponse);

    }

    @Nullable
    private ProgressListener getProgressListener() {
        Class<?>[] interfaces = this.getClass().getSuperclass().getInterfaces();
        System.out.println("interfaces = " + interfaces);
        System.out.println("this = " + this);
        ProgressListener progressListener = null;
        for (Class<?> anInterface : interfaces) {
            if (anInterface == ProgressListener.class) {
                progressListener = (ProgressListener) this;
                break;
            }
        }
        return progressListener;
    }

    protected abstract T bindData(String rawResponse) throws JSONException;

}
