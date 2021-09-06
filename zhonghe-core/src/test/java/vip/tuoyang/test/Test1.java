package vip.tuoyang.test;

import org.junit.jupiter.api.Test;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

/**
 * @author AlanSun
 * @date 2021/8/23 14:26
 */
public class Test1 {
    @Test
    public void test1Transfer() throws UnsupportedEncodingException {
        String plain = "你好";
        byte[] bytes = plain.getBytes("utf-8");

        byte[] bytes2 = new String(bytes).getBytes("gbk");
        final byte[] bytes3 = plain.getBytes("gbk");

        plain = new String(bytes2, "gbk");
    }

    @Test
    public void textTest() throws UnsupportedEncodingException {
        String a = "D6DCBDDCC2D7202D20D3CED4B0" +
                "BBE12E6D70";
        System.out.println(ServiceUtils.getContentFromHex(a));
//        final String gbk = new String(Objects.requireNonNull(ConvertCode.hexString2Bytes(a.replace(" ", ""))), "UTF-8");
//        System.out.println(gbk);
    }

    @Test
    public void textTest1() {
        String a = "443a5c6f70745c7a686f6e6768655c66696c655c546867704f26202d206e3856ed4f1a2e6d7033";
        String filePath = "D:\\opt\\zhonghe\\file\\周杰伦 - 游园会.mp3";
        final String s = ConvertCode.string2HexString(filePath);
        System.out.println(s);

        final String s1 = ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(filePath));
        System.out.println(s1);
        System.out.println(ServiceUtils.getContentFromHex(s1));
    }

    @Test
    public void testSplit() {
        String s = "01 00 00 00  11 CB D5 B4  F2 C2 CC 20  2D 20 C8 D5" +
                "      B9 E2 2E 6D  70 33 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  02 00 00 00  21 CB D5 B4  F2 C2 CC 20 " +
                "      2D 20 D4 D9  D3 F6 BC FB  28 43 43 54  56 D2 F4 C0 " +
                "      D6 C6 B5 B5  C0 29 2E 6D  70 33 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  03 00 00 00  13 CB D5 B4 " +
                "      F2 C2 CC 20  2D 20 D4 D9  D3 F6 BC FB  2E 6D 70 33 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  04 00 00 00 " +
                "      15 CD F5 C1  A6 BA EA 20  2D 20 B4 F3  B3 C7 D0 A1 " +
                "      B0 AE 2E 6D  70 33 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      05 00 00 00  1F CD F5 C1  A6 BA EA A1  A2 CC B7 CE " +
                "      AC CE AC 20  2D 20 D4 B5  B7 D6 D2 BB  B5 C0 C7 C5 " +
                "      2E 6D 70 33  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  06 00 00 00  1D D0 A1 C7  E0 C1 FA A1 " +
                "      A2 BB D4 D7  D3 20 2D 20  54 69 6D 65  28 4C 69 76 " +
                "      65 29 2E 6D  70 33 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  07 00 00 00  15 D6 DC BD " +
                "      DC C2 D7 20  2D 20 B0 D7  C9 AB B7 E7  B3 B5 2E 6D " +
                "      70 33 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  08 00 00 00 " +
                "      15 D6 DC BD  DC C2 D7 20  2D 20 B0 EB  B5 BA CC FA " +
                "      BA D0 2E 6D  70 33 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      09 00 00 00  11 D6 DC BD  DC C2 D7 20  2D 20 B2 CA " +
                "      BA E7 2E 6D  70 33 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  0A 00 00 00  11 D6 DC BD  DC C2 D7 20 " +
                "      2D 20 B5 BE  CF E3 2E 6D  70 33 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00  00 00 00 00  00 00 00 00 " +
                "      00 00 00 00  00 00 00 00";
        final List<String> strings = ServiceUtils.partContent2ListByLength(s.replace(" ", ""), 264);
        strings.forEach(System.out::println);
    }

    @Test
    public void checkSum() {
        final String s = "FE E0 A7 8A  11 10 00 00  B6 0E 05 00  0D 03 00 09 00 00 00 00 06";
        final int i = ServiceUtils.computeChkSum(s.replace(" ", ""));
        System.out.println(i);
    }
}
