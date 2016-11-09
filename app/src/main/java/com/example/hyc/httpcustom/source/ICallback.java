package com.example.hyc.httpcustom.source;

/**
 * Created by hyc on 16-11-9.
 */
public interface ICallback<T> {

    void onSuccessed(T response);

    void onFailure(Exception err);
}
