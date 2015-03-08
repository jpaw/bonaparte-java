package de.jpaw.bonaparte.core;

public class BonaClassAdapter {

    public static Long marshal(BonaPortableClass<?> cls) {
        return BonaPortableFactoryById.keyByIds(cls.getFactoryId(), cls.getId());
    }

    public static <E extends Exception> BonaPortableClass<?> unmarshal(Long key, ExceptionConverter<E> p) throws E {
        if (key == null)
            return null;
        BonaPortableClass<?> bclass = BonaPortableFactoryById.getByKey(key);
        if (bclass == null) {
            throw p.customExceptionConverter("Cannot find class for factoryId "
                    + BonaPortableFactoryById.factoryIdByKey(key) + ", classId " + BonaPortableFactoryById.classIdByKey(key), null);
        }
        return bclass;
    }
}
