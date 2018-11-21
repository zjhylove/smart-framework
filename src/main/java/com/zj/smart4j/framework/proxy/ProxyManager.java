package com.zj.smart4j.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * 代理管理类
 */
public class ProxyManager {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxies) {
        MethodInterceptor interceptor = (o, method, objects, methodProxy)
                -> new ProxyChain(targetClass, o, method, methodProxy, objects, proxies).doProxyChain();
        return (T) Enhancer.create(targetClass, interceptor);
    }
}
