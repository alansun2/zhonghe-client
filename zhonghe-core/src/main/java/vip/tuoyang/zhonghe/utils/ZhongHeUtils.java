package vip.tuoyang.zhonghe.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.base.util.AssertUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author AlanSun
 * @date 2021/8/23 9:54
 */
@Slf4j
public class ZhongHeUtils {

    /**
     * 交换顺序
     *
     * @param content    内容
     * @param groupCount 多少个字符为一组
     * @return 交换后的数据
     */
    public static String changeOrder(String content, int groupCount) {
        final int length = content.length() / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = length; i < length; i++, j--) {
            final String substring = content.substring(i * groupCount, i * groupCount + 2);
            sb.insert(0, substring);
        }

        return sb.toString();
    }

    /**
     * 计算校验和
     *
     * @param content 16 进制字符串
     * @return 2 字节的int值
     */
    public static int computeChkSum(String content) {
        final int length = content.length() / 2;
        int chkSum = 0;
        for (int i = 0; i < length; i++) {
            chkSum = chkSum + Integer.parseInt(content.substring(i * 2, i * 2 + 2), 16);
        }
        return chkSum & 0b11111111;
    }

    public static byte[] toGbkBytes(String content) {
        try {
            return content.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("转 gbk 失败");
        }
    }

    public static String getContentFromHex(String content) {
        try {
            return new String(Objects.requireNonNull(ConvertCode.hexString2Bytes(content)), "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error("16进制解析GBK失败: [{}]", content);
            log.error("16进制解析GBK失败", e);
            return "";
        }
    }

    /**
     * localDateTime 转 16 进制
     */
    public static String localDateTimeToHex(LocalDateTime localDateTime) {
        return StringUtils.leftPad(Integer.toHexString(localDateTime.getYear() % 2000), 2, '0') +
                StringUtils.leftPad(Integer.toHexString(localDateTime.getMonthValue()), 2, '0') +
                StringUtils.leftPad(Integer.toHexString(localDateTime.getDayOfMonth()), 2, '0') +
                StringUtils.leftPad(Integer.toHexString(localDateTime.getHour()), 2, '0') +
                StringUtils.leftPad(Integer.toHexString(localDateTime.getMinute()), 2, '0') +
                StringUtils.leftPad(Integer.toHexString(localDateTime.getSecond()), 2, '0');
    }

    /**
     * 把字符串分割成每段length长度
     * <p>
     * content的长度必须是length的倍数
     *
     * @param content    content
     * @param lengthEach 长度
     * @return size 为 length 的数组
     */
    public static List<String> partContent2ListByLength(String content, int lengthEach) {
        AssertUtils.isTrue(content.length() % lengthEach == 0, "content的长度必须是length的倍数");

        final int length = content.length() / lengthEach;
        List<String> contents = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final int startIndex = i * lengthEach;
            contents.add(content.substring(startIndex, startIndex + lengthEach));
        }
        return contents;
    }
}
