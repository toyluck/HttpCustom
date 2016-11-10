package com.example.hyc.httpcustom.source;

import java.util.PriorityQueue;

/**
 * Created by hyc on 2016/11/10.
 */

public class AppException extends Exception {

    private int       _statuCode;
    private String    _message;
    private Exception _err;

    public AppException(String message) {
        super(message);
        _message = message;
    }

    public AppException(int statuCode, String message) {
        super(message);
        _statuCode = statuCode;
        _message = message;
    }

    public AppException(Exception err) {
        _err = err;
    }

    @Override
    public String toString() {
        return "AppException{" +
                "_statuCode='" + _statuCode + '\'' +
                ", _message='" + _message + '\'' +
                ", _err=" + _err +
                '}';
    }

    public int getStatuCode() {
        return _statuCode;
    }

    public void setStatuCode(int statuCode) {
        _statuCode = statuCode;
    }

    @Override
    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public Exception getErr() {
        return _err;
    }

    public void setErr(Exception err) {
        _err = err;
    }
}
