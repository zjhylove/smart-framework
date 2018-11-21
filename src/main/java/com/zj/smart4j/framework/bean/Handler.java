package com.zj.smart4j.framework.bean;

import java.lang.reflect.Method;

/**
 * 封装Action信息
 */
public class Handler {

    /**
     * Controller 类
     */
    private Class<?> controllerClz;

    /**
     * Action 类
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClz,Method actionMethod){
        this.controllerClz = controllerClz;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClz() {
        return controllerClz;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
