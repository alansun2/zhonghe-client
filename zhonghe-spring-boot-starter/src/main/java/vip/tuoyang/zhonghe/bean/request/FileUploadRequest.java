package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/22 16:51
 */
@Getter
@Setter
public class FileUploadRequest {
    private String fileNo;

    private String fileName;

    private String contentBase64;
}
