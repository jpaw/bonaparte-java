package de.jpaw.bonaparte.jpa;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class UserTypeBonaparteIntegrator implements Integrator {

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // no-op
    }

    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        configuration.registerTypeOverride(new BonaPortableUserType(), new String[] { BonaPortableUserType.class.getName() });
        configuration.registerTypeOverride(new ByteArrayUserType(), new String[] { ByteArrayUserType.class.getName() });
    }

    @Override
    public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // no-op
    }

}
