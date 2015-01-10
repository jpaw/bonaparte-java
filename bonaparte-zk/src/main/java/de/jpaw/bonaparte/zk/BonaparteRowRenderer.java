package de.jpaw.bonaparte.zk;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

public class BonaparteRowRenderer implements ListitemRenderer<BonaPortable> {

    protected final MessageComposer<IOException> foldingComposer;
    protected final Map<Class<? extends BonaCustom>, List<String>> map;
    
    public BonaparteRowRenderer(CSVConfiguration cfg, List<String> columns) {
        // map = Collections.singletonMap(BonaPortable.class, columns);  // Java does not like this... (in Eclipse)
        map = new HashMap<Class<? extends BonaCustom>, List<String>>(2);
        map.put(BonaPortable.class, columns);
        foldingComposer = new FoldingComposer<IOException>(cfg == null ? new ZKComposer() : new ZKComposer(cfg), map, FoldingStrategy.TRY_SUPERCLASS);
    }

    public BonaparteRowRenderer(List<String> columns) {
        this(null, columns);
    }

    @Override
    public void render(Listitem listitem, BonaPortable data, int index) throws Exception {
        listitem.setValue(data);
        foldingComposer.writeRecord(data);
    }
}
