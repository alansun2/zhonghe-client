package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/22 14:15
 */
@Getter
@Setter
public class ZhongHeDto<T> {
    /**
     * 指令:
     * 1: init
     * 2: state
     * 3: 定时任务下发，
     * 4: 定时任务编辑；
     * 5: 定时任务删除;
     * 6: 可编辑任务新增
     * 7: 任务取消
     * 8: 上传媒体文件
     * 9: 删除媒体文件
     * 10: 获取播放终端列表
     * 11: 获取终端分组
     * 12: 重启
     * 13: 状态修改
     * 14：服务初始化
     * 15：中河软件更新
     * 16：自己更新
     * 17：文件更新
     */
    private Byte command;

    private String label;

    private T data;
}
