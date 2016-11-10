package com.example.hyc.httpcustom.source;

import android.annotation.TargetApi;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hyc on 2016/11/10.
 * 当一个请求数据过大时,直接使用Gson或者JsonObject将其读入内存很可能会造成OOM
 * 因此现将文件写入sd卡中,这里
 */

public abstract class JsonReaderCallback<T extends IEntity> extends AbstractCallback<T> {

    public JsonReaderCallback(String path) {
        setPath(path);
    }

    @Override
    protected T bindData(String path) throws JSONException, AppException {

        try {
            JsonReader jr = new JsonReader(new FileReader(path));

            return readToT(jr);

        } catch (Exception e) {
            throw new AppException(AppException.ExceptionType.OPERATION, e);
        }

    }

    private T readToT(JsonReader jr) throws IllegalAccessException, InstantiationException, IOException {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T    t    = ((Class<T>) type).newInstance();
        t.readObj(jr);
        return t;
    }


}
