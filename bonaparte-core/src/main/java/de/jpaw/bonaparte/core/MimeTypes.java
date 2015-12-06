package de.jpaw.bonaparte.core;

public interface MimeTypes {
    public final String MIME_TYPE_BONAPARTE         = "application/bonaparte";
    public final String MIME_TYPE_COMPACT_BONAPARTE = "application/cbon";
    public final String MIME_TYPE_JSON              = "application/json";
    public final String MIME_TYPE_XML               = "application/xml";

    public final String JSON_FIELD_FQON             = "@type";      // json-io compatible field name to define the class name
    public final String JSON_FIELD_PQON             = "@PQON";
}
