package com.example.hyc.httpcustom.source;


import org.json.JSONException;

/**
 * Created by hyc on 2016/11/10.
 */

public abstract class FileCallback extends AbstractCallback<String> implements ProgressListener {
    @Override
    protected String bindData(String rawResponse) throws JSONException {

        return rawResponse;
    }

    public FileCallback(String path) {

        setPath(path);
    }

    @Override
    public void onProgress(long currerntCount, long totalCount) {

    }
}
