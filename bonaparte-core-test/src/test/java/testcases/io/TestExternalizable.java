package testcases.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.io.Key;
import de.jpaw.bonaparte.pojos.io.MyObject;

public class TestExternalizable {
    
    private Object runIO(Externalizable org, Class<? extends Externalizable> type) throws Exception {
        // serialize
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        org.writeExternal(oos);
        oos.flush();
        byte [] serializedData = os.toByteArray();
        @SuppressWarnings("unused")
        String dataForDebugging = new String(serializedData);
        
        // deserialize
        Externalizable inst = type.newInstance();
        inst.readExternal(new ObjectInputStream(new ByteArrayInputStream(serializedData)));
        return inst;
    }
    
    
    @Test
    public void testExternalizable() throws Exception {
        MyObject org = new MyObject(new Key("bla!"), "Hello, world");
        Object moved = runIO(org, MyObject.class);
        assert(org.equals(moved));
    }
}
