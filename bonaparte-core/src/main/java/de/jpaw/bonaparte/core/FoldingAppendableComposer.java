package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

/** Delegates most output to the delegateComposer, but uses a permutation/selection of fields for the object output. */
public class FoldingAppendableComposer extends FoldingComposer<IOException> implements MessageComposer<IOException> {

    public FoldingAppendableComposer(AppendableComposer delegateComposer, Map<Class<? extends BonaCustom>, List<String>> mapping, FoldingStrategy errorStrategy) {
        super(delegateComposer, mapping, errorStrategy);
    }
}
