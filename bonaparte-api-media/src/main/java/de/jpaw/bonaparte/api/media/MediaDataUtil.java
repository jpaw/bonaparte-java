package de.jpaw.bonaparte.api.media;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.jpaw.bonaparte.pojos.api.media.MediaData;
import de.jpaw.bonaparte.pojos.api.media.MediaXType;
import de.jpaw.util.ByteArray;

/** Utilities, useful as Xtend extensions. */
public class MediaDataUtil {
    ////////////////////////////////////////////////////
    //
    // binary resources
    // for resources on the classpath, a leading "/" must be provided
    //
    ////////////////////////////////////////////////////

    public static ByteArray getBinaryResource(String path) throws IOException {
        InputStream fis = MediaDataUtil.class.getResourceAsStream(path);
        ByteArray result = ByteArray.fromInputStream(fis, 0);
        fis.close();
        return result;
    }

    public static MediaData getBinaryResource(String path, MediaXType type) throws IOException {
        MediaData result = new MediaData();
        result.setMediaType(type);
        result.setRawData(getBinaryResource(path));
        return result;
    }

    // further convenience APIs for a couple of frequently used media types

    public static MediaData resourceAsPNG(String path) throws IOException {
        return getBinaryResource(path, MediaTypes.MEDIA_XTYPE_PNG);
    }
    public static MediaData resourceAsJPG(String path) throws IOException {
        return getBinaryResource(path, MediaTypes.MEDIA_XTYPE_JPG);
    }
    public static MediaData resourceAsGIF(String path) throws IOException {
        return getBinaryResource(path, MediaTypes.MEDIA_XTYPE_GIF);
    }


    ////////////////////////////////////////////////////
    //
    // text resources
    // for resources on the classpath, no leading "/" should be provided
    //
    ////////////////////////////////////////////////////

    public static String getTextResource(String path) throws IOException {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }

    public static String getTextResource(String path, Charset charset) throws IOException {
        URL url = Resources.getResource(path);
        return Resources.toString(url, charset);
    }

    public static MediaData getTextResource(String path, MediaXType type) throws IOException {
        MediaData result = new MediaData();
        result.setMediaType(type);
        result.setText(getTextResource(path));
        return result;
    }

    public static MediaData getTextResource(String path, MediaXType type, Charset charset) throws IOException {
        MediaData result = new MediaData();
        result.setMediaType(type);
        result.setText(getTextResource(path, charset));
        return result;
    }

    // further convenience APIs for a couple of frequently used media types

    public static MediaData resourceAsText(String path) throws IOException {
        return getTextResource(path, MediaTypes.MEDIA_XTYPE_TEXT);
    }
    public static MediaData resourceAsHTML(String path) throws IOException {
        return getTextResource(path, MediaTypes.MEDIA_XTYPE_HTML);
    }
}
