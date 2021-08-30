package vip.tuoyang.zhonghe.support;

import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.service.ZhongHeClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final int holdCount = reentrantLock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙请稍后再试");
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
