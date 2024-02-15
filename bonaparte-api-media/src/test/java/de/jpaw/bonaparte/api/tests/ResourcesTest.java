package de.jpaw.bonaparte.api.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.api.media.MediaDataUtil;
import de.jpaw.bonaparte.pojos.api.media.MediaData;

public class ResourcesTest {

    @Test
    public void testPNG() throws Exception {
        MediaData d = MediaDataUtil.resourceAsPNG("/image/jpaw.png");
        Assertions.assertEquals(d.getRawData().length(), 1048);
    }

    @Test
    public void testJPG() throws Exception {
        MediaData d = MediaDataUtil.resourceAsJPG("/image/jpaw.jpg");
        Assertions.assertEquals(d.getRawData().length(), 1267);
    }

    @Test
    public void testText() throws Exception {
        MediaData d = MediaDataUtil.resourceAsText("text/hello.txt");
        Assertions.assertTrue(d.getText().startsWith("Hello, world"));
        Assertions.assertTrue(d.getText().length() <= 14);  // 13 on UNIX, 14 for CRLF on MS WIN
    }
}
