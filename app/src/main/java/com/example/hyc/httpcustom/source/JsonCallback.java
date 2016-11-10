package com.example.hyc.httpcustom.source;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hyc on 2016/11/10.
 */

public abstract class JsonCallback<T> extends AbstractCallback<T> implements ProgressListener {
    @Override
    protected T bindData(String rawResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(rawResponse);
        JSONObject data       = jsonObject.getJSONObject("data");

        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Gson gson = new Gson();
        return gson.fromJson(data.toString(), type);

    }

    @Override
    public void onProgress(long currerntCount, long totalCount) {

    }
}
