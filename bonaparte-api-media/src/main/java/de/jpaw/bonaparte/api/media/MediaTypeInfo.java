package de.jpaw.bonaparte.api.media;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;

import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.pojos.api.media.MediaCategory;
import de.jpaw.bonaparte.pojos.api.media.MediaType;
import de.jpaw.bonaparte.pojos.api.media.MediaTypeDescriptor;
import de.jpaw.bonaparte.pojos.api.media.MediaXType;
import de.jpaw.enums.XEnumFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/** Registry for media types. */
public class MediaTypeInfo {
    /** Maps the MIME type to the descriptor. */
    private static final ConcurrentMap<String, MediaTypeDescriptor> mimeMap = new ConcurrentHashMap<String, MediaTypeDescriptor>(32);

    /** Maps the Bonaparte (X)enum to the descriptor. */
    private static final ConcurrentMap<MediaXType, MediaTypeDescriptor> reverseMimeMap = new ConcurrentHashMap<MediaXType, MediaTypeDescriptor>(32);

    /** Maps the File extension to the descriptor. */
    private static final ConcurrentMap<String, MediaTypeDescriptor> extensionMap = new ConcurrentHashMap<String, MediaTypeDescriptor>(32);

    /** File extensions of various popular programming languages, used to map to text/plain. */
    private static final List<String> PROGRAMMING_LANGUAGE_EXTENSIONS = ImmutableList.of(
        "c", "h", "cpp", "cs", "m",         // C, C++, C#, Objective-C
        "java", "xtend", "js", "ts", "zul", // Java, Xtend, JavaScript, TypeScript, ZK screen definitions
        "py", "rs", "go",                   // Python, Rust, Go
        "sql", "sh", "csh", "ksh",          // SQL, Shell, C-Shell, KShell
        "bon", "bddl",                      // Bonaparte sources
        "pl", "php"                         // Perl, PHP
    );

    /** File extensions for various Java runtime bundles. */
    private static final List<String> JAVA_EXTENSIONS = ImmutableList.of("jar", "war");

    protected static void registerFormatType(MediaTypeDescriptor f) {
        f.freeze();  // make it immutable
        reverseMimeMap.putIfAbsent(f.getMediaType(), f);
        mimeMap.putIfAbsent(f.getMimeType(), f);
        extensionMap.putIfAbsent(f.getDefaultFileExtension(), f);
        if (f.getAdditionalExtensions() != null) {
            for (final String ext : f.getAdditionalExtensions()) {
                extensionMap.putIfAbsent(ext, f);
            }
        }
    }

    // static initializations
    static {
        final XEnumFactory<MediaXType> fct = MediaXType.myFactory;                         // fileExt,  category,              isText, mimeType
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.BONAPARTE),          "b",    MediaCategory.RECORDS,  true,  MimeTypes.MIME_TYPE_BONAPARTE));         // FIXME: made-up MIME type, should have been application/vnd.jpaw-bonaparte
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XML),                "xml",  MediaCategory.RECORDS,  true,  MimeTypes.MIME_TYPE_XML));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JSON),               "json", MediaCategory.RECORDS,  true,  MimeTypes.MIME_TYPE_JSON));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JSONL),              "jsonl", MediaCategory.RECORDS,  true,  "application/jsonl"));  // MIME type not yet standardized
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.COMPACT_BONAPARTE),  "cb",   MediaCategory.RECORDS,  false, MimeTypes.MIME_TYPE_COMPACT_BONAPARTE)); // FIXME: made-up MIME type, should have been application/vnd.jpaw-compact-bonaparte
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.YAML),               "yaml", MediaCategory.RECORDS,  true,  "application/yaml"));  // since Feb-2024: https://www.rfc-editor.org/rfc/rfc9512.html
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLS),                "xls",  MediaCategory.RECORDS,  false, "application/vnd.ms-excel"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XLSX),               "xlsx", MediaCategory.RECORDS,  false, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PPTX),               "pptx", MediaCategory.DOCUMENT, false, "application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.DOCX),               "docx", MediaCategory.DOCUMENT, false, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSV),                "csv",  MediaCategory.RECORDS,  true,  MimeTypes.MIME_TYPE_CSV));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEXT),               "txt",  MediaCategory.TEXT,     true,  "text/plain", PROGRAMMING_LANGUAGE_EXTENSIONS));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.HTML),               "html", MediaCategory.TEXT,     true,  "text/html"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.XHTML),              "xhtml",MediaCategory.TEXT,     true,  "application/xhtml+xml"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.CSS),                "css",  MediaCategory.TEXT,     true,  "text/css"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MARKDOWN),           "md",   MediaCategory.TEXT,     true,  "text/markdown"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TEX),                "tex",  MediaCategory.TEXT,     true,  "application/x-tex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.LATEX),              "latex",MediaCategory.TEXT,     true,  "application/x-latex"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.URL),                "url",  MediaCategory.TEXT,     true,  "application/x-uri"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PDF),                "pdf",  MediaCategory.DOCUMENT, false, "application/pdf"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.DVI),                "dvi",  MediaCategory.DOCUMENT, false, "application/x-dvi"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.POSTSCRIPT),         "ps",   MediaCategory.DOCUMENT, true,  "application/postscript"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.GIF),                "gif",  MediaCategory.IMAGE,    false, "image/gif"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.JPG),                "jpg",  MediaCategory.IMAGE,    false, "image/jpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.WEBP),               "webp", MediaCategory.IMAGE,    false, "image/webp"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.PNG),                "png",  MediaCategory.IMAGE,    false, "image/png"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.SVG),                "svg",  MediaCategory.IMAGE,    false, "image/svg+xml")); // set as binary to have same behavior of all image typed formats
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.FLAC),               "flac", MediaCategory.AUDIO,    false, "audio/flac"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.WAV),                "wav",  MediaCategory.AUDIO,    false, "audio/wav"));
        // registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP3),                "mp3",  MediaCategory.AUDIO,    false, "audio/mp3"));   // chrome, Opera  // is incorrect
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP3),                "mp3",  MediaCategory.AUDIO,    false, "audio/mpeg"));  // firefox, IE
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MP4),                "mp4",  MediaCategory.VIDEO,    false, "video/mp4"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.MPG),                "mpg",  MediaCategory.VIDEO,    false, "video/mpeg"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.RAW),                "dat",  MediaCategory.OTHER,    false, "application/octet-stream"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.FTL),                "ftl",  MediaCategory.TEXT,     true,  "application/x-freemarker"));
        // add some archives
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.ZIP),                "zip",  MediaCategory.CONTAINER, false, "application/zip", JAVA_EXTENSIONS));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.GZIP),               "gz",   MediaCategory.CONTAINER, false, "application/gzip"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.ZIP7),               "7z",   MediaCategory.CONTAINER, false, "application/x-7z-compressed"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.TAR),                "tar",  MediaCategory.CONTAINER, false, "application/x-tar"));
        registerFormatType(new MediaTypeDescriptor(fct.getByEnum(MediaType.UNDEFINED),          "bin",  MediaCategory.OTHER,     false, "application/octet-stream"));
    }

    /** Returns the MediaTypeDescriptor for a given MIME type, or returns null if none found. */
    @Nullable
    public static MediaTypeDescriptor getFormatByMimeType(@Nonnull final String mimeType) {
        return mimeMap.get(mimeType);
    }

    /** Returns the MediaTypeDescriptor for a given bonaparte type, or returns null if none found. */
    @Nullable
    public static MediaTypeDescriptor getFormatByType(@Nonnull final MediaXType formatType) {
        return reverseMimeMap.get(formatType);
    }

    /** Returns the MediaTypeDescriptor for a given file extension. */
    @Nullable
    public static MediaTypeDescriptor getFormatByFileExtension(@Nonnull final String mimeType) {
        return extensionMap.get(mimeType);
    }
}
