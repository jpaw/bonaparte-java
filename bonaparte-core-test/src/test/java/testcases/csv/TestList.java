package testcases.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.ListComposer;
import de.jpaw.bonaparte.pojos.csvTests.UnixPasswd;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

public class TestList {

    @Test
    public void testListComposer() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        UnixPasswd pwEntry2 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", "/home/jpaw", "/bin/bash");

        List<Object> storage = new ArrayList<Object>(10);
        ListComposer lc = new ListComposer(storage, false, false, false);
        lc.writeRecord(pwEntry);
        lc.writeRecord(pwEntry2);

        assert(storage.size() == 14);  // 2 entries
        assert(storage.get(2) instanceof Integer);
        assert(storage.get(7) instanceof String);
        assert(storage.get(9).equals(Integer.valueOf(1003)));

        lc.reset();
        lc.writeRecord(pwEntry2);
        assert(storage.size() == 7);  // 1 entry
    }

    @Test
    public void testListComposerWithFolding() throws Exception {
        UnixPasswd pwEntry2 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", "/home/jpaw", "/bin/bash");

        List<Object> storage = new ArrayList<Object>(10);
        ListComposer lc = new ListComposer(storage, false, false, false);

        List<String> fields = Arrays.asList( "gecos", "name", "shell");
        Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
        map.put(UnixPasswd.class, fields);
        FoldingComposer<RuntimeException> fld = new FoldingComposer<RuntimeException>(lc, map, FoldingStrategy.TRY_SUPERCLASS);
        fld.writeRecord(pwEntry2);

        assert(storage.size() == 3);  // 2 entries
        assert(storage.get(2) instanceof String);
        assert(storage.get(2).equals("/bin/bash"));
    }
}
