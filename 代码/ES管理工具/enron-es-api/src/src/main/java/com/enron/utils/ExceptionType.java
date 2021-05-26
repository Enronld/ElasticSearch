package com.enron.utils;

public class ExceptionType {

    //获取异常的类型
    public String getExceptionType(Exception e){
        return e.getMessage().substring(e.getMessage().indexOf("[")+1,e.getMessage().indexOf(",")).substring(5);
    }
}
