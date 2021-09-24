package vip.tuoyang.zhonghe.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.support.SyncSupport;

/**
 * @author AlanSun
 * @date 2021/9/23 11:22
 */
@Aspect
@Component
@ConditionalOnClass(value = RedissonClient.class)
public class SendClientLockAop {
    @Autowired
    private RedissonClient redissonClient;

    private static final String ZHONG_HE_SEND_CLIENT_LOCK = "ZHONG_HE_SEND_CLIENT_LOCK:";

    @Around(value = "execution(* vip.tuoyang.zhonghe.service.ZhongHeSendClient.*(..))")
    public Object lock(ProceedingJoinPoint pjp) throws Throwable {
        final String label = pjp.getArgs()[0].toString();
        final RLock lock = redissonClient.getLock(ZHONG_HE_SEND_CLIENT_LOCK + label);
        final int holdCount = lock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙，请稍后再试");
        }

        lock.lock();
        try {
            SyncSupport.getCountDownLatch2(label).reset();
            return pjp.proceed();
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }
}
