package de.jpaw.bonaparte.api.media;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.pojos.api.media.MediaCategory;
import de.jpaw.bonaparte.pojos.api.media.MediaType;
import de.jpaw.bonaparte.pojos.api.media.MediaTypeDescriptor;
import de.jpaw.bonaparte.pojos.api.media.MediaXType;
import de.jpaw.enums.XEnumFactory;

public class MediaTypeInfo {
    private static final ConcurrentMap<String, MediaTypeDescriptor> mimeMap = new ConcurrentHashMap<String, MediaTypeDescriptor>(32);
    private static final ConcurrentMap<MediaXType, MediaTypeDescriptor> reverseMimeMap = new ConcurrentHashMap<MediaXType, MediaTypeDescriptor>(32);

    protected static void registerFormatType(MediaTypeDescriptor f) {
        f.freeze();  // make it immutable
        reverseMimeMap.putIfAbsent(f.getMediaType(), f);
        mimeMap.putIfAbsent(f.getMimeType(), f);
    }

    // static initializations
    static {
        final XEnumFactory<MediaXType> fct = MediaXType.myFactory;  // fileExt, isImage. isAudio, isRecord, isText, isBinary, mimeType
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.BONAPARTE),          "bon",  MediaCategory.RECORDS, true,  MimeTypes.MIME_TYPE_BONAPARTE));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XML),                "xml",  MediaCategory.RECORDS, true,  MimeTypes.MIME_TYPE_XML));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JSON),               "json", MediaCategory.RECORDS, true,  MimeTypes.MIME_TYPE_JSON));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.COMPACT_BONAPARTE),  "cb",   MediaCategory.RECORDS, false, MimeTypes.MIME_TYPE_COMPACT_BONAPARTE));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLSX),               "xlsx", MediaCategory.RECORDS, false, "application/vnd.ms-excel"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLS),                "xls",  MediaCategory.RECORDS, false, "application/vnd.ms-excel"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSV),                "csv",  MediaCategory.RECORDS, true,  MimeTypes.MIME_TYPE_CSV));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEXT),               "txt",  MediaCategory.TEXT,    true,  "text/plain"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.HTML),               "html", MediaCategory.TEXT,    true,  "text/html"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSS),                "css",  MediaCategory.TEXT,    true,  "text/css"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MARKDOWN),           "md",   MediaCategory.TEXT,    true,  "text/markdown"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEX),                "tex",  MediaCategory.TEXT,    true,  "application/x-tex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.LATEX),              "latex",MediaCategory.TEXT,    true,  "application/x-latex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.URL),                "url",  MediaCategory.TEXT,    true,  "application/x-uri"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PDF),                "pdf",  MediaCategory.DOCUMENT,false, "application/pdf"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.DVI),                "dvi",  MediaCategory.DOCUMENT,false, "application/x-dvi"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.POSTSCRIPT),         "ps",   MediaCategory.DOCUMENT,true,  "application/postscript"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.GIF),                "gif",  MediaCategory.IMAGE,   false, "image/gif"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JPG),                "jpg",  MediaCategory.IMAGE,   false, "image/jpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PNG),                "png",  MediaCategory.IMAGE,   false, "image/png"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.WAV),                "wav",  MediaCategory.AUDIO,   false, "audio/x-wav"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP3),                "mp3",  MediaCategory.AUDIO,   false, "audio/mp3"));   // chrome, Opera
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP3),                "mp3",  MediaCategory.AUDIO,   false, "audio/mpeg"));  // firefox, IE
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP4),                "mp4",  MediaCategory.VIDEO,   false, "video/mp4"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MPG),                "mpg",  MediaCategory.VIDEO,   false, "video/mpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.RAW),                "dat",  MediaCategory.OTHER,   false, "application/octet-stream"));
    }

    public static MediaTypeDescriptor getFormatByMimeType(String mimeType) {
        return mimeMap.get(mimeType);
    }

    public static MediaTypeDescriptor getFormatByType(MediaXType formatType) {
        return reverseMimeMap.get(formatType);
    }
}
