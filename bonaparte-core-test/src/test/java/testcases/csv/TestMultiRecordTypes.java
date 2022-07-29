package testcases.csv;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.pojos.multiRecordTypes.EdgeRecord;
import de.jpaw.bonaparte.pojos.multiRecordTypes.EofRecord;
import de.jpaw.bonaparte.pojos.multiRecordTypes.HeadRecord;
import de.jpaw.bonaparte.pojos.multiRecordTypes.NodeRecord;
import de.jpaw.bonaparte.pojos.multiRecordTypes.RecordType;

public class TestMultiRecordTypes {
    private static final CSVConfiguration cfg = new CSVConfiguration.Builder().usingSeparator(";").usingQuoteCharacter(null).usingZeroPadding(false).build();

    private static final String RECORD_ID_HEAD = "HEAD";
    private static final String RECORD_ID_NODE = "NODE";
    private static final String RECORD_ID_EDGE = "EDGE";
    private static final String RECORD_ID_EOF = "EOF";

    private static Map<String, Class<? extends BonaPortable>> typeMap = new HashMap<String, Class<? extends BonaPortable>>(4);
    static {
        typeMap.put(RECORD_ID_HEAD, HeadRecord.class);
        typeMap.put(RECORD_ID_NODE, NodeRecord.class);
        typeMap.put(RECORD_ID_EDGE, EdgeRecord.class);
        typeMap.put(RECORD_ID_EOF, EofRecord.class);
    }


    private static final HeadRecord head = new HeadRecord(RECORD_ID_HEAD);
    private static final NodeRecord node1 = new NodeRecord(RECORD_ID_NODE, "Node 1");
    private static final NodeRecord node2 = new NodeRecord(RECORD_ID_NODE, "Node 2");
    private static final EdgeRecord edge = new EdgeRecord(RECORD_ID_EDGE, 1, 2);
    private static final EofRecord eof = new EofRecord(RECORD_ID_EOF);

    private static final RecordType [] dataToSerialize = {
        head, node1, node2, edge, eof
    };

    @Test
    public void testMultiRecord() throws Exception {
        // setup composer
        String [] results = new String [dataToSerialize.length];
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer2 cmp = new CSVComposer2(buffer, cfg);
        cmp.setWriteCRs(false);
        for (int i = 0; i < dataToSerialize.length; ++i) {
            cmp.reset();
            buffer.setLength(0);
            cmp.writeRecord(dataToSerialize[i]);
            results[i] = buffer.toString();
            // System.out.println("Created record " + results[i]);
        }

        // setup parser
        StringCSVParser p = new StringCSVParser(cfg, "", new StringCSVParser.DelimiterBasedObjectTypeDetector(typeMap, cfg.separator));
        for (int i = 0; i < dataToSerialize.length; ++i) {
            p.setSource(results[i]);
            BonaPortable result = p.readRecord();
            assert(result != null);
            assert(result.getClass() == dataToSerialize[i].getClass());
            assert(result.equals(dataToSerialize[i]));
        }
    }
}
