package de.jpaw.bonaparte.testrunner;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;

public abstract class AbstractTestrunner<T> {
    
    /** Serialize a record and return it. Optionally compare it with a provided expected result. */
    public abstract T serializationTest(BonaCustom src, T expectedResult) throws Exception;
    
    /** Deserialize some data and return the resulting object. Optionally compare it with a provided expected result. */
    public abstract BonaPortable deserializationTest(T src, BonaPortable expectedResult) throws Exception;
    
    /** Serialize some data and deserialize the result. Test that the resulting object equals the original one.
     * Optionally also compare with some expected serialized form. Returns the serialized form.
     * @param src
     * @param expectedResult
     * @throws Exception
     */
    public T serDeser(BonaPortable src, T expectedResult) throws Exception {
        T intermediateResult = serializationTest(src, expectedResult);
        deserializationTest(intermediateResult, src);
        return intermediateResult;
    }
    
    /** Deserialize some data and serialie the resulting object again. Optionally compare it with a provided expected result. */
    public BonaPortable deserSer(T src, BonaPortable expectedResult) throws Exception {
        BonaPortable intermediateResult = deserializationTest(src, expectedResult);
        serializationTest(intermediateResult, src);
        return intermediateResult;
    }
}

