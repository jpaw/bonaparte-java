package de.jpaw.bonaparte.refs;

import de.jpaw.util.ApplicationException;

public class PersistenceException extends ApplicationException {
    private static final long serialVersionUID = 61705245543364726L;

    private static final int ERROR_CODE_OFFSET = 13000;                                             // offset for all codes in this class
    private static final int OFFSET = (CL_DATABASE_ERROR * CLASSIFICATION_FACTOR) + ERROR_CODE_OFFSET; // offset for all codes in this class

    static public final int RECORD_DOES_NOT_EXIST        = OFFSET + 1;
    static public final int RECORD_ALREADY_EXISTS        = OFFSET + 2;
    static public final int DUPLICATE_UNIQUE_INDEX       = OFFSET + 3;
    static public final int UNKNOWN_INDEX_TYPE           = OFFSET + 4;
    static public final int NO_TRANSACTION               = OFFSET + 5;
    static public final int READONLY                     = OFFSET + 6;
    static public final int NO_RECORD_FOR_INDEX          = (CL_PARAMETER_ERROR * CLASSIFICATION_FACTOR) + ERROR_CODE_OFFSET + 7;
    static public final int NO_PRIMARY_KEY               = OFFSET + 8;
    static public final int RECORD_DOES_NOT_EXIST_ILE    = (CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + ERROR_CODE_OFFSET + 9;

    static {
        registerCode(RECORD_DOES_NOT_EXIST     , "No record for primary key found");
        registerCode(RECORD_ALREADY_EXISTS     , "Value for primary key already exists");
        registerCode(DUPLICATE_UNIQUE_INDEX    , "Value of unique index already exists");
        registerCode(UNKNOWN_INDEX_TYPE        , "Not known as a unique index");
        registerCode(NO_TRANSACTION            , "Not active transaction");
        registerCode(READONLY                  , "Entity is readonly via this API");
        registerCode(NO_RECORD_FOR_INDEX       , "No record has been found for the provided index");
        registerCode(NO_PRIMARY_KEY            , "No primary key provided for create or update operation");
        registerCode(RECORD_DOES_NOT_EXIST_ILE , "No record for primary key found, but this should not have happened");
    }

    private final long key;
    private final String entityName;
    private final String indexName;
    private final String indexValue;

    public PersistenceException(int errorCode, long key, String entityName) {
        this(errorCode, key, entityName, null, null);
    }

    public PersistenceException(int errorCode, long key, String entityName, String indexName, String indexValue) {
        super(errorCode, "Entity " + entityName
                + (key > 0L ? ", key " + key : "")
                + (indexName != null ? ", index " + indexName : "")
                + (indexValue != null ? ", index = " + indexValue : ""));
        this.key = key;
        this.entityName = entityName;
        this.indexName = indexName;
        this.indexValue = indexValue;
    }

    public long getKey() {
        return key;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexValue() {
        return indexValue;
    }
}
