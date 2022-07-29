package testcases.csv;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.pojos.csvTests.UnixPasswd;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;
import de.jpaw.bonaparte.util.FieldGetter;

public class TestFolding {

    private CSVConfiguration unixPasswdCfg = new CSVConfiguration.Builder().usingSeparator(":").usingQuoteCharacter(null).build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput,
            Map<Class<? extends BonaCustom>, List<String>> map) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, cfg);
        cmp.setWriteCRs(false);
        FoldingComposer<IOException> fld = new FoldingComposer<IOException>(cmp, map, FoldingStrategy.TRY_SUPERCLASS);
        try {
            fld.writeRecord(input);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        String actualOutput = buffer.toString();
        System.out.println(actualOutput);
        assert(expectedOutput.equals(actualOutput));
    }

    @Test
    public void testUnixPasswd() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        UnixPasswd pwEntry2 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", "/home/jpaw", "/bin/bash");
        List<String> fields = Arrays.asList( "gecos", "name", "shell");
        Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
        map.put(UnixPasswd.class, fields);
        runTest(unixPasswdCfg, pwEntry, "System superuser:root:/bin/sh\n", map);
        runTest(unixPasswdCfg, pwEntry2, "Michael Bischoff:jpaw:/bin/bash\n",  map);
    }

    @Test
    public void testMetaData() throws Exception {
        List<String> fields = Arrays.asList( "name", "fields.name", "numberOfFields");
        Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
        map.put(ClassDefinition.class, fields);
        runTest(unixPasswdCfg, UnixPasswd.class$MetaData(), "csvTests.UnixPasswd:name:passwd:uid:gid:gecos:dir:shell:7\n",  map);
    }

    @Test
    public void testMetaDataWithIndex() throws Exception {
        List<String> fields = Arrays.asList( "name", "fields[2].name", "numberOfFields");
        Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
        map.put(ClassDefinition.class, fields);
        runTest(unixPasswdCfg, UnixPasswd.class$MetaData(), "csvTests.UnixPasswd:uid:7\n",  map);
    }

    @Test
    public void testSingleFieldAccess() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        assert("root".equals(FieldGetter.getField(pwEntry, "name")));
    }
    @Test
    public void testSingleFieldAccess2() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        assert("/bin/sh".equals(FieldGetter.getField(pwEntry, "shell")));
    }
}
