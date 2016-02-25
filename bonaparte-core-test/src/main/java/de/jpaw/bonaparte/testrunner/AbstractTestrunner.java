package de.jpaw.bonaparte.testrunner;

import java.io.IOException;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.util.ApplicationException;

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

    /** Deserialize the input, but expect an error, which is either a MessageParserException (of specified code), or an IOException, in case the latter in null. */
    public void expectDeserializationError(T src, Integer errorCode) throws Exception {
        try {
            deserializationTest(src, null);
            throw new Exception("Expected an exception here");
        } catch (Exception e) {
            if (errorCode == null) {
                if (e instanceof IOException) {
                    // OK
                    return;
                }
                throw new Exception("Expected an IOException here, but got " + e);
            }
            if (!(e instanceof ApplicationException)) {
                throw new Exception("Expected an ApplicationException here, but got " + e);
            }
            ApplicationException ae = (ApplicationException)e;
            if (ae.getErrorCode() == errorCode.intValue()) {
                // OK
                return;
            }
            throw new Exception("Got an ApplicationException as expected, but wanted " + errorCode + " and got " + ae);
        }
    }
}
