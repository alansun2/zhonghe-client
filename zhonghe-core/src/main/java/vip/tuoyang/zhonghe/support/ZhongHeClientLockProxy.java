package vip.tuoyang.zhonghe.support;

import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.service.SendClient;
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
    private final ZhongHeClient proxyed;

    private final String label;

    public ZhongHeClientLockProxy(ZhongHeClient proxyed, String label) {
        this.proxyed = proxyed;
        this.label = label;
    }

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public static final ThreadLocal<SendClient> ZHONG_HE_CLIENT_THREAD_LOCAL = new InheritableThreadLocal<>();
    public static final ThreadLocal<String> LABEL_THREAD_LOCAL = new InheritableThreadLocal<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final int holdCount = reentrantLock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙请稍后再试");
        }
        ZHONG_HE_CLIENT_THREAD_LOCAL.set(proxyed.getSendClient());
        LABEL_THREAD_LOCAL.set(label);
        reentrantLock.lock();
        try {
            return method.invoke(proxyed, args);
        } finally {
            reentrantLock.unlock();
            ZHONG_HE_CLIENT_THREAD_LOCAL.remove();
            LABEL_THREAD_LOCAL.remove();
        }
    }

    public static ZhongHeClient getProxy(ZhongHeClient zhongTaiClient, String label) {
        return (ZhongHeClient) Proxy.newProxyInstance(zhongTaiClient.getClass().getClassLoader(), zhongTaiClient.getClass().getInterfaces(), new ZhongHeClientLockProxy(zhongTaiClient, label));
    }
}