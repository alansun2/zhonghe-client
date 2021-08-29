package vip.tuoyang.zhonghe.utils;

import java.util.Arrays;

/**
 * @author AlanSun
 * @date 2021/8/22 15:26
 */
public class ByteOrderUtils {

    /**
     * 将整数按照小端存放，低字节出访低位
     */
    public static byte[] toLh(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);

        if (n < 0b1111111111111111) {
            b = Arrays.copyOfRange(b, 0, 2);
        }
        return b;
    }

    /**
     * 将int转为大端，低字节存储高位
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] toHh(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        if (n < 0b1111111111111111) {
            b = Arrays.copyOfRange(b, 0, 2);
        }
        return b;
    }
}
