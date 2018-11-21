package com.zj.smart4j.framework.helper;

import com.zj.smart4j.framework.annotation.Inject;
import com.zj.smart4j.framework.util.CollectionUtil;
import com.zj.smart4j.framework.util.ReflectionUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入助手类
 */
public class IocHelper {

    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                Class<?> beanClz = beanEntry.getKey();
                Object bean = beanEntry.getValue();
                Field[] fields = beanClz.getDeclaredFields();
                if (ArrayUtils.isNotEmpty(fields)) {
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Inject.class)) {
                            Class<?> type = field.getType();
                            Object o = beanMap.get(type);
                            if (o != null) {
                                ReflectionUtil.setField(bean, field, o);
                            }
                        }
                    }
                }
            }
        }
    }
}
