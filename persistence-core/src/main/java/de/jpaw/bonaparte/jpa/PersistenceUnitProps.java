package de.jpaw.bonaparte.jpa;

import java.util.Map;

/** Various utility methods to retrieve properties from the persistence.xml.
 * Some cases are straightforward, others depend on the OR mapper used.
 *
 * A bit portability issue is, that the availability of these properties is OR mapper specific:
 *
 * With Eclipselink, the properties obtained from the EMF are empty, while the properties obtained from an EntityManager 1:1 match the persistence.xml entries
 *  (including the database JDBC password!)
 *
 * With Hibernate, the properties obtained from the EMF are a mixture of system properties implicit internal properties plus the provided ones,
 *  however the JDBC password is asterisked out. (****)
 * From the EntityManager, we get 5 properties here (as of Hibernate 4.2.2):
 * - jakarta.persistence.lock.timeout
 * - jakarta.persistence.cache.storeMode
 * - jakarta.persistence.cache.retrieveMode
 * - jakarta.persistence.lock.scope
 * - org.hibernate.flushMode
 *
 * Additionally supplied own properties can be retrieved for both OR mappers.
 */
public class PersistenceUnitProps {

    /** Just a shorthand to get the JDBC driver name from the persistence.xml. */
    public static String getJdbcDriver(Map<String,Object> properties) {
        return (String)properties.get("jakarta.persistence.jdbc.driver");
    }
    /** Just a shorthand to get the JDBC connection URL from the persistence.xml. */
    public static String getJdbcUrl(Map<String,Object> properties) {
        return (String)properties.get("jakarta.persistence.jdbc.url");
    }
    /** Just a shorthand to get the JDBC user name from the persistence.xml. */
    public static String getJdbcUserId(Map<String,Object> properties) {
        return (String)properties.get("jakarta.persistence.jdbc.user");
    }
    /** Just a shorthand to get the JDBC user's password from the persistence.xml. */
    public static String getJdbcPassword(Map<String,Object> properties) {
        return (String)properties.get("jakarta.persistence.jdbc.password");
    }

    // access to the "provider" is not possible? So we have to guess it from other properties.
    // One which is always available is the database
    public static ORMapper getORMapper(Map<String,Object> properties) {
        if (properties.get("eclipselink.target-database") != null)
            return ORMapper.ECLIPSELINK;
        if (properties.get("hibernate.dialect") != null)
            return ORMapper.HIBERNATE;
        return ORMapper.UNSUPPORTED;
    }

    private static String normalized(Object dbo) {
        if (dbo instanceof String) {
            // strip off any fully qualified class name prefix
            String db = (String)dbo;
            int pos = db.lastIndexOf(".");
            if (pos > 0)
                db = db.substring(pos+1);
            return db;
        }
        if (dbo instanceof Enum) {
            // convert it to its token
            return ((Enum<?>)dbo).name();
        }
        // some other class: use the simple class name
        return dbo.getClass().getSimpleName();
    }

    /** Returns a guess of the underlying database vendor. */
    public static DatabaseFlavour getDatabaseFlavour(Map<String,Object> properties) {
        Object dbo = properties.get("eclipselink.target-database");
        if (dbo != null) {
            String db = normalized(dbo);
            if ("PostgreSQL".equals(db))
                return DatabaseFlavour.POSTGRES;
            if (db.startsWith("Oracle"))
                return DatabaseFlavour.ORACLE;
            if ("SQLServer".equals(db))
                return DatabaseFlavour.MSSQLSERVER;
            if ("DB2".equals(db))
                return DatabaseFlavour.DB2;
            if ("Derby".equals(db))
                return DatabaseFlavour.DERBY;
            if ("MySQL".equals(db))
                return DatabaseFlavour.MYSQL;
            if ("com.sap.persistence.platform.database.HDBPlatform".equals(db))
                return DatabaseFlavour.SAPHANA;
            return DatabaseFlavour.UNSUPPORTED;
        }
        dbo = properties.get("hibernate.dialect");
        if (dbo != null) {
            String db = normalized(dbo);
            if ("PostgreSQLDialect".equals(db))
                return DatabaseFlavour.POSTGRES;
            if ("PostgresPlusDialect".equals(db))
                return DatabaseFlavour.POSTGRES;
            if (db.startsWith("Oracle"))
                return DatabaseFlavour.ORACLE;
            if ("SQLServer2008Dialect".equals(db))
                return DatabaseFlavour.MSSQLSERVER;
            if ("DB2Dialect".equals(db))
                return DatabaseFlavour.DB2;
            if ("DerbyDialect".equals(db))
                return DatabaseFlavour.DERBY;
            if ("H2Dialect".equals(db))
                return DatabaseFlavour.H2;
            if ("MySQL5Dialect".equals(db))
                return DatabaseFlavour.MYSQL;
            if ("MySQL5InnoDBDialect".equals(db))
                return DatabaseFlavour.MYSQL;
            if ("HANARowStoreDialect".equals(db))       // requires Hibernate 4.3.x
                return DatabaseFlavour.SAPHANA;
            if ("HANAColumnStoreDialect".equals(db))    // requires Hibernate 4.3.x
                return DatabaseFlavour.SAPHANA;
            return DatabaseFlavour.UNSUPPORTED;
        }
        return DatabaseFlavour.UNSUPPORTED;
    }
}
