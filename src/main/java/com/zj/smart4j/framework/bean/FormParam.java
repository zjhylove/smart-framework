package com.zj.smart4j.framework.bean;

/**
 * 封装表单参数
 */
public class FormParam {

    private String filedName;
    private Object filedValue;

    public FormParam(String filedName, Object filedValue) {
        this.filedName = filedName;
        this.filedValue = filedValue;
    }

    public String getFiledName() {
        return filedName;
    }

    public Object getFiledValue() {
        return filedValue;
    }
}
