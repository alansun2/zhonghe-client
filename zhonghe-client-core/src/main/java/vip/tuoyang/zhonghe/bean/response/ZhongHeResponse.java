package vip.tuoyang.zhonghe.bean.response;

import lombok.Getter;
import lombok.Setter;
import vip.tuoyang.zhonghe.constants.AcceptEnum;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

/**
 * @author AlanSun
 * @date 2021/8/28 15:12
 */
@Getter
@Setter
public class ZhongHeResponse {
    /**
     * 16 进制
     */
    private String deviceId;
    /**
     * 帧数
     */
    private int sn;
    /**
     * 16字节之后的长度
     */
    private int len;
    /**
     * 命令 {@link CmdEnum}
     */
    private CmdEnum cmdEnum;
    /**
     * cmd 的参数
     */
    private String para;
    /**
     * acceot {@link AcceptEnum}
     */
    private AcceptEnum acceptEnum;
    /**
     * 16字节之后的内容
     */
    private String content;
    /**
     * 原始数据
     */
    private String originalData;

    public static ZhongHeResponse parse(String receiveContent) {
        ZhongHeResponse zhongHeResponse = new ZhongHeResponse();
        zhongHeResponse.setOriginalData(receiveContent);
        zhongHeResponse.setDeviceId(ServiceUtils.changeOrder(receiveContent.substring(8, 15), 2));
        zhongHeResponse.setSn(Integer.parseInt(ServiceUtils.changeOrder(receiveContent.substring(16, 20), 2), 16));
        zhongHeResponse.setLen(Integer.parseInt(ServiceUtils.changeOrder(receiveContent.substring(20, 24), 2), 16));
        zhongHeResponse.setCmdEnum(CmdEnum.valueBy(receiveContent.substring(24, 26)));
        zhongHeResponse.setPara(receiveContent.substring(26, 28));
        zhongHeResponse.setAcceptEnum(AcceptEnum.valueBy(receiveContent.substring(28, 30)));
        zhongHeResponse.setContent(receiveContent.substring(32, 32 + zhongHeResponse.getLen()));
        return zhongHeResponse;
    }

    /**
     * 是否是最后一帧
     *
     * @return true: 是; false: 否
     */
    public boolean isLastSn() {
        long end = 0b10000000000000000L;
        return (sn & end) == end;
    }
}
