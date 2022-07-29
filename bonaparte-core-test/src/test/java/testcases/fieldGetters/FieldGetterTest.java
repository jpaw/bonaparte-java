package testcases.fieldGetters;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.fieldGettersTest.Child;
import de.jpaw.bonaparte.pojos.fieldGettersTest.Parent;
import de.jpaw.bonaparte.util.FieldGetter;

public class FieldGetterTest {

    @Test
    public void testFieldGettersWithNullComponents() throws Exception {
        Child c = new Child(47);
        Parent p = new Parent();

        p.setX(48);

        Assertions.assertEquals(FieldGetter.getField(p, "x"), 48);          // test simple access

        // child access
        Assertions.assertEquals(FieldGetter.getField(p, "y.x"), null);      // test access when a path component is null
        p.setY(c);
        Assertions.assertEquals(FieldGetter.getField(p, "y.x"), 47);        // test simple access to child

        // list access
        Assertions.assertEquals(FieldGetter.getField(p, "list[2].x"), null);        // test access via null List
        p.setList(Collections.singletonList(c));
        Assertions.assertEquals(FieldGetter.getField(p, "list[0].x"), 47);          // test access via existing List entry
        Assertions.assertEquals(FieldGetter.getField(p, "list[-1].x"), 47);         // test access via existing List but invalid index: assumes index 0
        Assertions.assertEquals(FieldGetter.getField(p, "list[99].x"), null);       // test access via existing List but index out of range

        // map access
        Assertions.assertEquals(FieldGetter.getField(p, "map[hello].x"), null);     // test access via null Map
        p.setMap(Collections.singletonMap("hello", c));
        Assertions.assertEquals(FieldGetter.getField(p, "map[not].x"), null);       // test access via null entry
        Assertions.assertEquals(FieldGetter.getField(p, "map[hello].x"), 47);       // test access via Map existing entry
        Assertions.assertEquals(FieldGetter.getField(p, "map[hello]"), 47);         // test access via Map to object => in this case the object is serialized, the first element is returned
        Assertions.assertEquals(FieldGetter.getFieldOrObj(p, "map[hello]"), c);     // test access via Map to object => in this case the object is returned
    }
}
