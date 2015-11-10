package de.jpaw.bonaparte.api.media;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
        if (reverseMimeMap.putIfAbsent(f.getMediaType(), f) != null) {
            // was a new entry: also add the reverse mapping
            mimeMap.putIfAbsent(f.getMimeType(), f);
        }
    }
    
    // static initializations
    static {
        final XEnumFactory<MediaXType> fct = MediaXType.myFactory;  // fileExt, isImage. isAudio, isRecord, isText, isBinary, mimeType
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.BONAPARTE),          "bon",  MediaCategory.RECORDS, "application/bonaparte"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XML),                "xml",  MediaCategory.RECORDS, "application/xml"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JSON),               "json", MediaCategory.RECORDS, "application/json"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.COMPACT_BONAPARTE),  "cb",   MediaCategory.RECORDS, "application/combo"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLSX),               "xlsx", MediaCategory.RECORDS, "application/vnd.ms-excel"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLS),                "xls",  MediaCategory.RECORDS, "application/vnd.ms-excel"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSV),                "csv",  MediaCategory.RECORDS, "text/csv"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEXT),               "txt",  MediaCategory.TEXT,    "text/plain"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.HTML),               "html", MediaCategory.TEXT,    "text/html"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSS),                "css",  MediaCategory.TEXT,    "text/css"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MARKDOWN),           "md",   MediaCategory.TEXT,    "text/markdown"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEX),                "tex",  MediaCategory.TEXT,    "application/x-tex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.LATEX),              "latex",MediaCategory.TEXT,    "application/x-latex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PDF),                "pdf",  MediaCategory.DOCUMENT,"application/pdf"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.DVI),                "dvi",  MediaCategory.DOCUMENT,"application/x-dvi"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.POSTSCRIPT),         "ps",   MediaCategory.DOCUMENT,"application/postscript"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.GIF),                "gif",  MediaCategory.IMAGE,   "image/gif"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JPG),                "jpg",  MediaCategory.IMAGE,   "image/jpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PNG),                "png",  MediaCategory.IMAGE,   "image/png"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.WAV),                "wav",  MediaCategory.AUDIO,   "audio/x-wav"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP3),                "mp3",  MediaCategory.AUDIO,   "audio/mpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP4),                "mp4",  MediaCategory.VIDEO,   "video/mpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MPG),                "mpg",  MediaCategory.VIDEO,   "video/mpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.RAW),                "dat",  MediaCategory.OTHER,   "application/octet-stream"));
    }

    public static MediaTypeDescriptor getFormatByMimeType(String mimeType) {
        return mimeMap.get(mimeType);
    }
    
    public static MediaTypeDescriptor getFormatByType(MediaXType formatType) {
        return mimeMap.get(formatType);
    }
}
