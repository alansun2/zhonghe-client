package vip.tuoyang.zhonghe.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vip.tuoyang.base.util.HttpClientUtils;
import vip.tuoyang.base.util.IpUtils;
import vip.tuoyang.base.util.bean.HttpParams;
import vip.tuoyang.zhonghe.bean.ZhongHeConfig;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

/**
 * @author AlanSun
 * @date 2021/9/3 13:11
 */
@Slf4j
@Component
public class IpSchedule {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private ObjectMapper objectMapper;

    private String lastIp;

    /**
     * ip 改变监听
     */
    public void ipChangeListen() {
        final String publicIp = IpUtils.getPublicIp();
        if (lastIp == null || !lastIp.equals(publicIp)) {
            final ZhongHeConfig zhongHeConfig = serviceSystemProperties.getZhongHeConfig();
            IpChangeRequest ipChangeRequest = new IpChangeRequest();
            ipChangeRequest.setIp(publicIp);
            ipChangeRequest.setLabel(zhongHeConfig.getLabel());
            try {
                final HttpParams.HttpParamsBuilder builder = HttpParams.builder().headers(ArrayUtils.toArray(new BasicHeader("secret", serviceSystemProperties.getSecret())))
                        .url(serviceSystemProperties.getServerUrl() + serviceSystemProperties.getPath().getIpChange())
                        .httpEntity(new StringEntity(objectMapper.writeValueAsString(ipChangeRequest), ContentType.APPLICATION_JSON));
                final HttpResponse httpResponse = HttpClientUtils.doPost(builder.build());
                final StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.OK.value()) {
                    lastIp = publicIp;
                }
            } catch (Exception e) {
                log.error("上报IP失败, ip: [{}]", publicIp);
                log.error("上报IP失败", e);
            }
        }
    }
}
