package com.example.hyc.httpcustom.source;

import java.util.PriorityQueue;


/**
 * Created by hyc on 2016/11/10.
 * 框架所有的异常处理类
 */

public class AppException extends Exception {

    /**
     * 将请求时遇到的各种异常分门别类
     * 在所有抛出 AppException 类的地方都需要来标注该异常的类别
     * todo 添加更多的处理逻辑
     */
    public enum ExceptionType {
        REQ_TIMEOUT, SERVECE, MANUL,RES_TIMEOUT,OPERATION,CANCELD
    }

    public ExceptionType getExceptionType() {
        return _exceptionType;
    }

    private int       _statuCode;
    private String    _message;
    private Exception _err;
    ExceptionType _exceptionType;

    public AppException(ExceptionType type, String message) {
        super(message);
        _message = message;
        _exceptionType = type;
    }

    public AppException(ExceptionType type, int statuCode, String message) {
        super(message);
        _statuCode = statuCode;
        _message = message;
        _exceptionType = type;
    }

    public AppException(ExceptionType type, Exception err) {
        _err = err;
        _exceptionType = type;
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
