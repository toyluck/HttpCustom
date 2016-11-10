package com.example.hyc.httpcustom.source;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.net.HttpURLConnection;

/**
 * Created by hyc on 16-11-9.
 */
public interface ICallback<T> {

    @UiThread
    void onSuccessed(T response);

    @UiThread
    void onFailure(AppException err);

    @WorkerThread
    T parse(HttpURLConnection connection) throws AppException;


    void cancelReq();

    /**
     * 在数据返回到主线程前进行操作
     */
    @WorkerThread
    void postRequest(T t);

    /**
     * 在请求网络数据前调用
     *
     * @return
     */
    @WorkerThread
    T checkAndGetDataFromCache();


    /**
     * @param t 从 {@link #checkAndGetDataFromCache()} 中获取的数据
     */
    @UiThread
    void onFromCache(T t);
}
