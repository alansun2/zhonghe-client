package vip.tuoyang.zhonghe.service.downloadhandler;

import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AlanSun
 * @date 2021/8/27 8:50
 * <p>
 * 播放终端解析
 */
public class PlayerDataDownloadHandler implements DownLoadResultHandler {

    private PlayerDataDownloadHandler() {
    }

    private static final PlayerDataDownloadHandler HANDLER = new PlayerDataDownloadHandler();

    public static PlayerDataDownloadHandler getInstance() {
        return HANDLER;
    }

    /**
     * 返回数据处理
     *
     * @param contentStr contentStr
     */
    @Override
    public void handler(String para, String contentStr) {
        final List<String> contents = ServiceUtils.partContent2ListByLength(contentStr, 80);
        final List<TerminalDataResponse> terminalDataResponses = contents.stream().map(s -> {
            TerminalDataResponse terminalDataResponse = new TerminalDataResponse();
            terminalDataResponse.setTerminalNo(ServiceUtils.changeOrder(s.substring(0, 8), 2));
            terminalDataResponse.setTerminalStatus(Byte.parseByte(s.substring(8, 10), 16));
            final int length = Integer.parseInt(s.substring(16, 18), 16);
            if (length != 0) {
                int startIndex = 18;
                int endIndex = length * 2 + startIndex;
                terminalDataResponse.setTerminalName(ServiceUtils.getContentFromHex(s.substring(startIndex, endIndex)));
            } else {
                terminalDataResponse.setTerminalName("未命名");
            }
            return terminalDataResponse;
        }).collect(Collectors.toList());

        ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.labelDownloadResultMap.get(para);
        if (zhongHeDownloadResult == null) {
            zhongHeDownloadResult = new ZhongHeDownloadResult();
            zhongHeDownloadResult.setData(terminalDataResponses);
            SyncResultSupport.labelDownloadResultMap.put(para, zhongHeDownloadResult);
        } else {
            final List<TerminalDataResponse> data = (List<TerminalDataResponse>) zhongHeDownloadResult.getData();
            data.addAll(terminalDataResponses);
        }
    }
}
