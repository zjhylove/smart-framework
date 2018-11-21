package com.zj.smart4j.framework;

import com.zj.smart4j.framework.helper.*;
import com.zj.smart4j.framework.util.ClassUtil;

/**
 * 加载相应的Helper类
 */
public class HelperLoader {

    public static void init() {
        Class<?>[] classes = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class,
        };
        for (Class<?> clz : classes) {
            ClassUtil.loadClass(clz.getName(), true);
        }
    }
}
