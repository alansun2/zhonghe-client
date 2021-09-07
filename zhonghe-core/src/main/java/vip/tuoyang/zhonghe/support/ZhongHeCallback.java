package vip.tuoyang.zhonghe.support;

import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @date 2021/9/1 15:23
 */
public interface ZhongHeCallback {
    /**
     * 广播服务状态修改时回调
     *
     * @param label        label
     * @param stateHandler 状态处理器
     */
    void callback(String label, SendStateHandler stateHandler);

    @Slf4j
    class DefaultCallback implements ZhongHeCallback {

        /**
         * 广播服务状态修改时回调
         *
         * @param label        label
         * @param stateHandler 状态处理器
         */
        @Override
        public void callback(String label, SendStateHandler stateHandler) {
            log.error("label: [{}]", label);
        }
    }
}