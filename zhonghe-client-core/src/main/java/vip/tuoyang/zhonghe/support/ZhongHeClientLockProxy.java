package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.service.ZhongHeClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author AlanSun
 * @date 2021/8/29 11:12
 */
public class ZhongHeClientLockProxy implements InvocationHandler {
    private final Object proxyed;

    public ZhongHeClientLockProxy(Object proxyed) {
        this.proxyed = proxyed;
    }

    private final Map<String, ReentrantLock> methodNameLockMap = new ConcurrentHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String name = method.getName();
        ReentrantLock reentrantLock = methodNameLockMap.get(name);
        if (reentrantLock == null) {
            reentrantLock = new ReentrantLock();
            methodNameLockMap.put(name, reentrantLock);
        }
        reentrantLock.lock();
        try {
            return method.invoke(proxyed, args);
        } finally {
            reentrantLock.unlock();
        }
    }

    public static ZhongHeClient getProxy(ZhongHeClient zhongTaiClient) {
        return (ZhongHeClient) Proxy.newProxyInstance(zhongTaiClient.getClass().getClassLoader(), zhongTaiClient.getClass().getInterfaces(), new ZhongHeClientLockProxy(zhongTaiClient));
    }
}
