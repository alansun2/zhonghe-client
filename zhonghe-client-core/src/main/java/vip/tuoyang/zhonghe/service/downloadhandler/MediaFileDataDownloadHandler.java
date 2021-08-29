package vip.tuoyang.zhonghe.service.downloadhandler;

import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AlanSun
 * @date 2021/8/27 8:50
 */
public class MediaFileDataDownloadHandler implements DownLoadResultHandler {

    private MediaFileDataDownloadHandler() {
    }

    private static final MediaFileDataDownloadHandler HANDLER = new MediaFileDataDownloadHandler();

    public static MediaFileDataDownloadHandler getInstance() {
        return HANDLER;
    }

    /**
     * 返回数据处理
     *
     * @param contentStr contentStr
     */
    @Override
    public void handler(String para, String contentStr) {
        final List<String> contents = ServiceUtils.partContent2ListByLength(contentStr, 264);
        final List<MediaFileDataResponse> mediaFileDataResponses = contents.stream().map(s -> {
            MediaFileDataResponse mediaFileDataResponse = new MediaFileDataResponse();
            mediaFileDataResponse.setNo(ServiceUtils.changeOrder(s.substring(0, 4), 2));
            int startIndex = 10;
            int endIndex = Integer.parseInt(s.substring(8, 10), 16) * 2;
            mediaFileDataResponse.setMediaFileName(ServiceUtils.getContentFromHex(s.substring(startIndex, startIndex + endIndex)));
            return mediaFileDataResponse;
        }).collect(Collectors.toList());

        ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.downloadParaResultMap.get(para);
        if (zhongHeDownloadResult == null) {
            zhongHeDownloadResult = new ZhongHeDownloadResult();
            zhongHeDownloadResult.setData(mediaFileDataResponses);
            SyncResultSupport.downloadParaResultMap.put(para, zhongHeDownloadResult);
        } else {
            final List<MediaFileDataResponse> data = (List<MediaFileDataResponse>) zhongHeDownloadResult.getData();
            data.addAll(mediaFileDataResponses);
        }
    }
}
