package vip.tuoyang.zhonghe.support;

import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.constants.StateEnum;
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

    public ZhongHeClientLockProxy(ZhongHeClient proxyed) {
        this.proxyed = proxyed;
    }

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public static final ThreadLocal<SendClient> ZHONG_HE_CLIENT_THREAD_LOCAL = new InheritableThreadLocal<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final int holdCount = reentrantLock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙请稍后再试");
        }
        ZHONG_HE_CLIENT_THREAD_LOCAL.set(proxyed.getSendClient());
        reentrantLock.lock();
        try {
            if (!"initMiddleWare".equals(method.getName()) && !"state".equals(method.getName()) && !"close".equals(method.getName())) {
                final ZhongHeResult<StateResponse> state = proxyed.state();
                if (!state.getData().getState().equals(StateEnum.ONLINE_RUNNING)) {
                    throw new BizException(state.getData().getState().getDesc());
                }
            }

            return method.invoke(proxyed, args);
        } finally {
            reentrantLock.unlock();
            ZHONG_HE_CLIENT_THREAD_LOCAL.remove();
        }
    }

    public static ZhongHeClient getProxy(ZhongHeClient zhongTaiClient, String label) {
        return (ZhongHeClient) Proxy.newProxyInstance(zhongTaiClient.getClass().getClassLoader(), zhongTaiClient.getClass().getInterfaces(), new ZhongHeClientLockProxy(zhongTaiClient));
    }
}
