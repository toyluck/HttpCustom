package com.example.hyc.httpcustom;

import com.example.hyc.httpcustom.source.AbstractCallback;

import org.json.JSONException;

/**
 * Created by hyc on 2016/11/10.
 */

public abstract class FileCallback extends AbstractCallback<String> {
    @Override
    protected String bindData(String rawResponse) throws JSONException {

        return rawResponse;
    }

    public FileCallback(String path) {
        setPath(path);
    }
}
