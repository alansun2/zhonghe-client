package vip.tuoyang.zhonghe.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author AlanSun
 * @date 2021/8/22 13:43
 **/
@Slf4j
public class ConvertCode {
    /**
     * 字节数组转16进制字符串
     *
     * @param b 字节数组
     * @return 16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        String hex;
        for (byte value : b) {
            hex = Integer.toHexString(value & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param hex 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String hex) {
        if (hex.length() < 2) {
            return null;
        }

        int l = hex.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }

        return ret;
    }

    /**
     * 字符串转16进制字符串
     *
     * @param strPart 字符串
     * @return 16进制字符串
     */
    public static String string2HexString(String strPart) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * 16进制字符串转字符串
     *
     * @param src 16进制字符串
     * @return 字节数组
     */
    public static String hexString2String(String src) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < src.length() / 2; i++) {
            temp.append((char) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue());
        }
        return temp.toString();
    }

    /**
     * 字符转成字节数据char-->integer-->byte
     */
    public static Byte char2Byte(Character src) {
        return Integer.valueOf((int) src).byteValue();
    }

    /**
     * 10进制数字转成16进制
     *
     * @param a   转化数据
     * @param len 占用字节数
     */
    public static String intToHexString(int a, int len) {
        len <<= 1;
        StringBuilder hexString = new StringBuilder(Integer.toHexString(a));
        int b = len - hexString.length();
        if (b > 0) {
            for (int i = 0; i < b; i++) {
                hexString.insert(0, "0");
            }
        }
        return hexString.toString();
    }

    /**
     * 将16进制的2个字符串进行异或运算
     * http://blog.csdn.net/acrambler/article/details/45743157
     *
     * @param strHexX 16进制字符串 1
     * @param strHexY 注意：此方法是针对一个十六进制字符串一字节之间的异或运算，如对十五字节的十六进制字符串异或运算：1312f70f900168d900007df57b4884
     *                先进行拆分：13 12 f7 0f 90 01 68 d9 00 00 7d f5 7b 48 84
     *                13 xor 12-->1
     *                1 xor f7-->f6
     *                f6 xor 0f-->f9
     *                ....
     *                62 xor 84-->e6
     *                即，得到的一字节校验码为：e6
     */
    public static String xor(String strHexX, String strHexY) {
        //将x、y转成二进制形式
        StringBuilder anotherBinary = new StringBuilder(Integer.toBinaryString(Integer.valueOf(strHexX, 16)));
        StringBuilder thisBinary = new StringBuilder(Integer.toBinaryString(Integer.valueOf(strHexY, 16)));
        StringBuilder result = new StringBuilder();
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary.insert(0, "0");
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary.insert(0, "0");
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i)) {
                result.append("0");
            } else {
                result.append("1");
            }
        }
        return Integer.toHexString(Integer.parseInt(result.toString(), 2));
    }


    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytes2Str(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * @return 接收字节数据并转为16进制字符串
     */
    public static String receiveHexToString(byte[] by) {
        try {
            String str = bytes2Str(by);
            assert str != null;
            str = str.toLowerCase();
            return str;
        } catch (Exception ex) {
            log.error("接收字节数据并转为16进制字符串异常", ex);
        }
        return null;
    }

    /**
     * "7dd",4,'0'==>"07dd"
     *
     * @param input  需要补位的字符串
     * @param size   补位后的最终长度
     * @param symbol 按symol补充 如'0'
     * @return N_TimeCheck中用到了
     */
    public static String fill(String input, int size, char symbol) {
        StringBuilder inputBuilder = new StringBuilder(input);
        while (inputBuilder.length() < size) {
            inputBuilder.insert(0, symbol);
        }
        input = inputBuilder.toString();
        return input;
    }
    
    /**
     * 十进制转二进制，指定位长度，不足补零
     *
     * @param value  十进制
     * @param length 位长度
     * @return 二进制
     */
    public static String int2BinString(int value, int length) {
        return StringUtils.leftPad(Integer.toBinaryString(value), length, '0');
    }

    /**
     * 二进制字符串转16进制字符串
     *
     * @param value 二进制字符串
     * @return 16进制字符串
     */
    public static String binStr2HexString(String value, int length) {
        return intToHexString(Integer.parseInt(value, 2), length);
    }
}