package vip.tuoyang.zhonghe.exception;

/**
 * @author AlanSun
 * @date 2021/9/7 13:38
 */
public class TimeOutException extends RuntimeException {

    public TimeOutException() {
        super("中间件超时异常，可能未启动");
    }
}
