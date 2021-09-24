package vip.tuoyang.zhonghe.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.support.SyncSupport;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author AlanSun
 * @date 2021/9/23 11:22
 */
@Aspect
@Component
@ConditionalOnMissingClass(value = {"org.redisson.api.RedissonClient"})
public class SendClientLockV2Aop {
    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Around(value = "execution(* vip.tuoyang.zhonghe.service.ZhongHeSendClient.*(..))")
    public Object lock(ProceedingJoinPoint pjp) throws Throwable {
        final String label = pjp.getArgs()[0].toString();
        final int holdCount = reentrantLock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙，请稍后再试");
        }

        reentrantLock.lock();
        try {
            SyncSupport.getCountDownLatch2(label).reset();
            return pjp.proceed();
        } finally {
            reentrantLock.unlock();
        }
    }
}
