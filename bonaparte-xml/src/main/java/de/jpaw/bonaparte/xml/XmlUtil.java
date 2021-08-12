package de.jpaw.bonaparte.xml;

import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.reflections.Reflections;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.xenums.init.ReflectionsPackageCache;

public class XmlUtil {
    private static final Class<?> [] TYPE_DUMMY = new Class [0];

    public static JAXBContext getJaxbContext(String ... packages) {

        Reflections [] reflections = ReflectionsPackageCache.getAll(packages);
        Set<Class<?>> classes = new HashSet<Class<?>>(1000);
        for (Reflections r: reflections) {
            for (Class<? extends BonaPortable> cls : r.getSubTypesOf(BonaPortable.class)) {
                if (!cls.isInterface())
                    classes.add(cls);
            }
        }
        classes.add(XmlListWrapper.class);
        try {
            return JAXBContext.newInstance(classes.toArray(TYPE_DUMMY));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
