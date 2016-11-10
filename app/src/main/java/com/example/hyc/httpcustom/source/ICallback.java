package com.example.hyc.httpcustom.source;

import java.net.HttpURLConnection;

/**
 * Created by hyc on 16-11-9.
 */
public interface ICallback<T> {

    void onSuccessed(T response);

    void onFailure(AppException err);
    T parse(HttpURLConnection connection) throws AppException;
}
