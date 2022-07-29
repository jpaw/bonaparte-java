package testcases.generator;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.access.*;

public class TestAccessTypes {
    static private final int ACCESS_MODIFIERS = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;

    static private void testClass(Class<? extends BonaPortable> clazz, int defaultType) throws NoSuchFieldException, SecurityException {
        assert (clazz.getDeclaredField("isDefault").getModifiers()   & ACCESS_MODIFIERS) == defaultType;
        assert (clazz.getDeclaredField("isPrivate").getModifiers()   & ACCESS_MODIFIERS) == Modifier.PRIVATE;
        assert (clazz.getDeclaredField("isProtected").getModifiers() & ACCESS_MODIFIERS) == Modifier.PROTECTED;
        assert (clazz.getDeclaredField("isPublic").getModifiers()    & ACCESS_MODIFIERS) == Modifier.PUBLIC;
    }

    @Test
    public void testAccessTypes() throws Exception {
        testClass(NoDefaults.class,       0);
        testClass(DefaultPrivate.class,   Modifier.PRIVATE);
        testClass(DefaultProtected.class, Modifier.PROTECTED);
        testClass(DefaultPublic.class,    Modifier.PUBLIC);
    }

    @Test
    public void testAccessTypesWT() throws Exception {
        testClass(NoDefaultsWT.class,       0);
        testClass(DefaultPrivateWT.class,   Modifier.PRIVATE);
        testClass(DefaultProtectedWT.class, Modifier.PROTECTED);
        testClass(DefaultPublicWT.class,    Modifier.PUBLIC);
    }

    @Test
    public void testTypeAccess() throws Exception {
        assert ((Inherited.class.getDeclaredField("shouldBeDefault").getModifiers() & ACCESS_MODIFIERS) == 0);
        assert ((Inherited.class.getDeclaredField("shouldBePrivate").getModifiers() & ACCESS_MODIFIERS) == Modifier.PRIVATE);
    }
}
