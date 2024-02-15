package de.jpaw.bonaparte.jpa;

import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConverter {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);

    protected boolean isPostgres(Session session) {
        Object platform = session.getDatasourcePlatform();
        final boolean isPostgres = platform != null && "PostgreSQLPlatform".equals(platform.toString());
        LOGGER.info("Postgres platform detected for {}? {}", getClass().getSimpleName(), isPostgres);
        return isPostgres;
    }
}
