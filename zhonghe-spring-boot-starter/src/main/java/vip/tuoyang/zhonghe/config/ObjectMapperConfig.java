package vip.tuoyang.zhonghe.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import vip.tuoyang.base.codec.jackson.JacksonCustomLocalDateDeserializer;
import vip.tuoyang.base.codec.jackson.JacksonCustomLocalDateSerializer;
import vip.tuoyang.base.codec.jackson.JacksonCustomLocalDateTimeDeserializer;
import vip.tuoyang.base.codec.jackson.JacksonCustomLocalDateTimeSerializer;
import vip.tuoyang.base.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author AlanSun
 * @date 2021/9/23 9:51
 */
public class ObjectMapperConfig {

    private static volatile ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (ObjectMapperConfig.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    JavaTimeModule javaTimeModule = new JavaTimeModule();
                    javaTimeModule.addSerializer(LocalDate.class, new JacksonCustomLocalDateSerializer());
                    javaTimeModule.addDeserializer(LocalDate.class, new JacksonCustomLocalDateDeserializer(DateTimeFormatter.ofPattern(DateUtils.DATE_DAY_FORMAT)));
                    javaTimeModule.addSerializer(LocalDateTime.class, new JacksonCustomLocalDateTimeSerializer());
                    javaTimeModule.addDeserializer(LocalDateTime.class, new JacksonCustomLocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT)));
                    objectMapper.registerModule(javaTimeModule);
                }
            }
        }

        return objectMapper;
    }
}
