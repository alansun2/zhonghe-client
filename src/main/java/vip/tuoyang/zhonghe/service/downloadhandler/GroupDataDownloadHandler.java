package vip.tuoyang.zhonghe.service.downloadhandler;

import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/27 8:50
 */
public class GroupDataDownloadHandler implements DownLoadResultHandler {

    private GroupDataDownloadHandler() {
    }

    private static final GroupDataDownloadHandler HANDLER = new GroupDataDownloadHandler();

    public static GroupDataDownloadHandler getInstance() {
        return HANDLER;
    }

    /**
     * 返回数据处理
     *
     * @param contentStr contentStr
     */
    @Override
    public void handler(String para, String contentStr) {
        List<GroupDataResponse> groupDataResponseList = new ArrayList<>();
        do {
            GroupDataResponse groupDataResponse = new GroupDataResponse();
            groupDataResponse.setNo(ServiceUtils.changeOrder(contentStr.substring(0, 8), 2));
            final int len = Integer.parseInt(contentStr.substring(8, 10), 16);
            groupDataResponse.setGroupName(ServiceUtils.getContentFromHex(contentStr.substring(10, 10 + len * 2)));

            List<String> terminalNos = new ArrayList<>();
            final String nosStr = contentStr.substring(36 * 2);
            final int length = nosStr.length() / 8;
            int i = 0;
            for (; i < length; i++) {
                final String no = nosStr.substring(i * 8, (i + 1) * 8);
                if ("00000000".equals(no)) {
                    break;
                }
                terminalNos.add(ServiceUtils.changeOrder(no, 2));
            }
            groupDataResponse.setTerminalNos(terminalNos);
            groupDataResponseList.add(groupDataResponse);
            contentStr = contentStr.substring(72 + length * 8);
        } while (contentStr.length() != 0);


        ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.downloadParaResultMap.get(para);
        if (zhongHeDownloadResult == null) {
            zhongHeDownloadResult = new ZhongHeDownloadResult();
            zhongHeDownloadResult.setData(groupDataResponseList);
            SyncResultSupport.downloadParaResultMap.put(para, zhongHeDownloadResult);
        } else {
            final List<GroupDataResponse> data = (List<GroupDataResponse>) zhongHeDownloadResult.getData();
            data.addAll(groupDataResponseList);
        }
    }
}
