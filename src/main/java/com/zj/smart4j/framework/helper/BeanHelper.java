package com.zj.smart4j.framework.helper;

import com.zj.smart4j.framework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * bean 助手类
 */
public class BeanHelper {

    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> beanClass : beanClassSet) {
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass, obj);
        }
    }

    /**
     * 设置bean
     *
     * @param clz
     * @param obj
     */
    public static void setBean(Class<?> clz, Object obj) {
        BEAN_MAP.put(clz, obj);
    }

    /**
     * 获取bean映射
     *
     * @return
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 获取bean实例
     *
     * @param clz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clz) {
        if (!BEAN_MAP.containsKey(clz)) {
            throw new RuntimeException("can not get bean by class:" + clz);
        }
        return (T) BEAN_MAP.get(clz);
    }
}
