package com.example.hyc.httpcustom.source;

/**
 * Created by hyc on 16-11-9.
 */
public class Request {

    private RequestMethod method;

    private int    _timeout;
    private int    _readTimeout;
    private String _content;

    public void globalCatch(AppException err) {
        if (_globalRequestListerner == null || !_globalRequestListerner.globalCatchException(err)) {
            _iCallback.onFailure(err);
        }
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

    public enum RequestMethod {
        GET, POST, PUT, DELETE
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

    Request(String url, RequestMethod method, int timeout, int readTimeout, String content) {
        this.url = url;
        this.method = method;
        _timeout = timeout;
        _readTimeout = readTimeout;
        _content = content;
        _content = content;
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
        private int           _timeout;
        private int           _readTimeout;
        private RequestMethod _method;
        private String        _url;
        private String        content;

        public Build() {
            _timeout = 10 * 1000;
            _readTimeout = 20 * 1000;
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


        public Request build() {
            if (null == _url || "".equals(_url)) {
                throw new NullPointerException("url can't be null");
            }
            return new Request(_url, _method, _timeout, _readTimeout, content);
        }

    }
}
