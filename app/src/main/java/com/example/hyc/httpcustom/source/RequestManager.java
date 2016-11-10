package com.example.hyc.httpcustom.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2016/11/10.
 * 管理请求
 */

public class RequestManager {
    private HashMap<String, ArrayList<Request>> _cacheRequests = new HashMap<>();

    public void addRequestAndExecute(Request request) {
        if (_cacheRequests.containsKey(request.getTag())) {
            ArrayList<Request> requests = _cacheRequests.get(request.getTag());
            if (!requests.contains(request)) {
                requests.add(request);
            }
        } else {
            _cacheRequests.put(request.getTag(), new ArrayList<>(Arrays.asList(request)));
        }
        request.execute();
    }

    public void cancelRequestByTag(String tag) {
        if (_cacheRequests.containsKey(tag)) {
            // 针对内存的问题 需不需要将已经cancel的请求清理出集合呢?
            cancelRequests(_cacheRequests.get(tag));
            _cacheRequests.remove(tag);
        }
    }

    private void cancelRequests(ArrayList<Request> requests) {
        for (Request request : requests) {
            if (!request.isCanceled())
                request.setCanceled(true);
        }
    }

    public void cancelAll() {
        for (Map.Entry<String, ArrayList<Request>> entry : _cacheRequests.entrySet()) {
            cancelRequests(entry.getValue());
        }
        _cacheRequests.clear();
    }
}
