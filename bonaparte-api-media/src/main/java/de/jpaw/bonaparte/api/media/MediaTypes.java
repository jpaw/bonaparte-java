package de.jpaw.bonaparte.api.media;

import de.jpaw.bonaparte.pojos.api.media.MediaType;
import de.jpaw.bonaparte.pojos.api.media.MediaXType;

public interface MediaTypes {
    public static final MediaXType MEDIA_XTYPE_BONAPARTE         = MediaXType.myFactory.getByEnum(MediaType.BONAPARTE);
    public static final MediaXType MEDIA_XTYPE_XML               = MediaXType.myFactory.getByEnum(MediaType.XML);
    public static final MediaXType MEDIA_XTYPE_JSON              = MediaXType.myFactory.getByEnum(MediaType.JSON);
    public static final MediaXType MEDIA_XTYPE_COMPACT_BONAPARTE = MediaXType.myFactory.getByEnum(MediaType.COMPACT_BONAPARTE);
    public static final MediaXType MEDIA_XTYPE_XLSX              = MediaXType.myFactory.getByEnum(MediaType.XLSX);
    public static final MediaXType MEDIA_XTYPE_XLS               = MediaXType.myFactory.getByEnum(MediaType.XLS);
    public static final MediaXType MEDIA_XTYPE_CSV               = MediaXType.myFactory.getByEnum(MediaType.CSV);
    public static final MediaXType MEDIA_XTYPE_TEXT              = MediaXType.myFactory.getByEnum(MediaType.TEXT);
    public static final MediaXType MEDIA_XTYPE_HTML              = MediaXType.myFactory.getByEnum(MediaType.HTML);
    public static final MediaXType MEDIA_XTYPE_CSS               = MediaXType.myFactory.getByEnum(MediaType.CSS);
    public static final MediaXType MEDIA_XTYPE_MARKDOWN          = MediaXType.myFactory.getByEnum(MediaType.MARKDOWN);
    public static final MediaXType MEDIA_XTYPE_TEX               = MediaXType.myFactory.getByEnum(MediaType.TEX);
    public static final MediaXType MEDIA_XTYPE_LATEX             = MediaXType.myFactory.getByEnum(MediaType.LATEX);
    public static final MediaXType MEDIA_XTYPE_PDF               = MediaXType.myFactory.getByEnum(MediaType.PDF);
    public static final MediaXType MEDIA_XTYPE_DVI               = MediaXType.myFactory.getByEnum(MediaType.DVI);
    public static final MediaXType MEDIA_XTYPE_POSTSCRIPT        = MediaXType.myFactory.getByEnum(MediaType.POSTSCRIPT);
    public static final MediaXType MEDIA_XTYPE_GIF               = MediaXType.myFactory.getByEnum(MediaType.GIF);
    public static final MediaXType MEDIA_XTYPE_JPG               = MediaXType.myFactory.getByEnum(MediaType.JPG);
    public static final MediaXType MEDIA_XTYPE_PNG               = MediaXType.myFactory.getByEnum(MediaType.PNG);
    public static final MediaXType MEDIA_XTYPE_WAV               = MediaXType.myFactory.getByEnum(MediaType.WAV);
    public static final MediaXType MEDIA_XTYPE_MP3               = MediaXType.myFactory.getByEnum(MediaType.MP3);
    public static final MediaXType MEDIA_XTYPE_MP4               = MediaXType.myFactory.getByEnum(MediaType.MP4);
    public static final MediaXType MEDIA_XTYPE_MPG               = MediaXType.myFactory.getByEnum(MediaType.MPG);
    public static final MediaXType MEDIA_XTYPE_RAW               = MediaXType.myFactory.getByEnum(MediaType.RAW);
}
