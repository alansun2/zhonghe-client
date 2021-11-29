import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import vip.tuoyang.base.util.MultipleThreadDownloadManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/10/13 15:27
 */
public class TT {
    @Test
    public void main() throws IOException {
        MultipleThreadDownloadManager multipleThreadDownloadManager = new MultipleThreadDownloadManager(16, 1024 * 1024 * 60, true);
        multipleThreadDownloadManager.start("http://oss_dev.tuoyang.vip:28090/seip/file/20211008/8a7e365baf3941d2881c52deb705f0ec.mp3", new File("D:\\tt.mp3"));

        final long start = System.currentTimeMillis();
        FileUtils.copyURLToFile(new URL("http://oss_dev.tuoyang.vip:28090/seip/file/20211008/8a7e365baf3941d2881c52deb705f0ec.mp3"), new File("D:\\tt2.mp3"));
        System.out.println(System.currentTimeMillis() - start);
        LockSupport.park();
    }

    @Test
    public void test1() throws IOException {
        Runtime.getRuntime().exec("cmd /c start D:\\project\\jszn\\broadcast\\zhonghe-broadcast\\test.bat");
//        LockSupport.park();
    }
}
