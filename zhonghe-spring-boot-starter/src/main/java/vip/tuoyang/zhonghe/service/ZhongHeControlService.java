package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import vip.tuoyang.base.util.HttpClientUtils;
import vip.tuoyang.base.util.bean.HttpParams;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/18 8:47
 */
@Slf4j
@Service
public class ZhongHeControlService {
    @Autowired
    private ZhongHeConnectionManager zhongHeConnectionManager;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * 重启 nas 和中间件
     *
     * @param label label
     */
    public boolean reboot(String label) {
        final ZhongHeClient zhongHeClient = zhongHeConnectionManager.getZhongHeClient(label);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        headers.add(new BasicHeader("secret", serviceSystemProperties.getSecret()));
        final HttpParams httpParams = HttpParams.builder().url("http://" + zhongHeClient.getZhongHeConfig().getNasIp() + ":8084" + "/common/reboot").headers(headers).build();
        try {
            final HttpResponse httpResponse = HttpClientUtils.doPost(httpParams);
            final StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                return true;
            } else {
                log.error("重启失败: label: [{}], httpResponse: [{}]", label, EntityUtils.toString(httpResponse.getEntity(), Charset.defaultCharset()));
                return false;
            }
        } catch (IOException e) {
            log.error("重启失败: label: [{}]", label);
            log.error("重启失败:", e);
            return false;
        }
    }
}
