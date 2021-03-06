package com.example.hyc.httpcustom.source;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hyc on 16-11-9.
 */
public class Request {
    private static final RequestTask REQUEST_TASK = new RequestTask();
    private final Context _context;

    private RequestMethod method;
    private int           _maxRetryCount;
    private int           _timeout;
    private int           _readTimeout;
    private String        _content;
    private volatile AtomicBoolean _canceled = new AtomicBoolean(false);
    private String _tag;


    public void setCanceled(boolean canceled) {
        _canceled.set(canceled);
        _iCallback.cancelReq();
    }

    public boolean isCanceled() {
        return _canceled.get();
    }

    public void checkCanceld() throws AppException {
        //跟Activity bind lifecycle
        if (_canceled.getAndSet(false) || (_context != null && ((AppCompatActivity) _context).isFinishing())) {
            _iCallback.cancelReq();
            throw new AppException(AppException.ExceptionType.CANCELD, "Request has been canceld");
        }

    }


    public void globalCatch(AppException err) {
        if (_globalRequestListerner == null || !_globalRequestListerner.globalCatchException(err)) {
            _iCallback.onFailure(err);
        }
    }

    public void execute() {
        REQUEST_TASK.addTask(this);
    }

    private GlobalRequestErrListerner _globalRequestListerner;

    /**
     * 添加全局的RequestErr处理 ,用来处理比如说 login token的实效等
     *
     * @param globalRequestListerner
     */
    public void setGlobalRequestListerner(GlobalRequestErrListerner globalRequestListerner) {
        _globalRequestListerner = globalRequestListerner;
    }


    public void setTag(String tag) {
        _tag = tag;
    }

    public String getTag() {
        return _tag;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE
    }

    public int getMaxRetryCount() {
        return _maxRetryCount;
    }

    public int getReadTimeout() {
        return _readTimeout;
    }

    public int getTimeout() {
        return _timeout;
    }

    public String getUrl() {
        return url;
    }

    String url;

    Request(Context context, String url, RequestMethod method, int timeout, int readTimeout, String content, int maxRetryCount) {
        _context = context;
        this.url = url;
        this.method = method;
        _timeout = timeout;
        _readTimeout = readTimeout;
        _content = content;
        _content = content;
        _maxRetryCount = maxRetryCount;
    }

    public ICallback _iCallback;

    public void setiCallback(ICallback iCallback) {
        _iCallback = iCallback;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getMethodName() {
        return method.name();
    }


    public String getContent() {
        return _content;
    }

    public static class Build {
        private final Context       _context;
        private       int           _timeout;
        private       int           _readTimeout;
        private       RequestMethod _method;
        private       String        _url;
        private       String        content;
        private int _maxRetryCount = 5;

        public Build(Context context) {
            _context = context;
            _timeout = 1 * 1000;
            _readTimeout = 1 * 1000;
            _method = RequestMethod.GET;
        }

        public Build setContent(String content) {
            this.content = content;
            return this;
        }

        public Build baseUrl(String url) {
            _url = url;
            return this;
        }

        public Build method(RequestMethod method) {
            _method = method;
            return this;
        }

        public Build setTimeout(int timeout) {
            _timeout = timeout;
            return this;
        }

        public Build setReadTimeout(int readTimeout) {
            _readTimeout = readTimeout;
            return this;
        }

        public Build retry(int count) {
            _maxRetryCount = count;
            return this;
        }

        public Request build() {
            if (null == _url || "".equals(_url)) {
                throw new NullPointerException("url can't be null");
            }
            return new Request(_context, _url, _method, _timeout, _readTimeout, content, _maxRetryCount);
        }

    }
}
