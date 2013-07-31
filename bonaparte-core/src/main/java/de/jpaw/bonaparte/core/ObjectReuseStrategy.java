package de.jpaw.bonaparte.core;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Specifies the backreferencing strategy for some serializers.
 * The possible values are
 * <li>NONE - do not check for recurring objects (will destroy identities)
 * <li>BY_REFERENCE - check for recurring objects using the object reference identity (leaves identities untouched)
 * <li>BY_CONTENTS - check for recurring objects using object equality (possibly creates new identities)
 * It depends on the data, which approach is the fastest. In any case, the serialized size is smallest for BY_CONTENTS, followed by BY_REFERENCE.
 *
 */
public enum ObjectReuseStrategy {
    NONE, BY_REFERENCE, BY_CONTENTS;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectReuseStrategy.class);
    static public ObjectReuseStrategy defaultStrategy = NONE;
    static {
        // read from properties file
        String path = "/META-INF/maven/de.jpaw/bonaparte-core/pom.properties";
        String version = "UNKNOWN";
        
        Properties prop = new Properties();
        InputStream in = ObjectReuseStrategy.class.getResourceAsStream(path );
        try {
            prop.load(in);
            version = prop.getProperty("version");
        } catch (Exception e) {
            // have no properties => version is unknown
            LOGGER.debug("cannot access maven properties: ", e);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                
            }
        }

        // read the system default via property, if allowed
        try {
            String sysDefault = System.getProperty("bonaparte.defaultReuseStrategy");
            if (sysDefault != null) {
                if ("BY_REFERENCE".equals(sysDefault))
                    defaultStrategy = BY_REFERENCE;
                else if ("BY_CONTENTS".equals(sysDefault))
                    defaultStrategy = BY_CONTENTS;
                else if ("BY_CONTENTS".equals(sysDefault))
                    defaultStrategy = BY_CONTENTS;
                else if ("NONE".equals(sysDefault))
                    defaultStrategy = NONE;
                else
                    LOGGER.warn("Unknown system default reuseStrategy: {}", sysDefault);
            }
        } catch (Exception e) {
            // low log level, as this is an optional feature and warnings may be annoying
            LOGGER.debug("cannot access system properties");
        }
        LOGGER.info("Starting BONAPARTE-java version {} using reuse strategy {}", version, defaultStrategy);
    }
}
