package de.jpaw.bonaparte.core;

import java.util.List;
import java.util.Map;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

/** Delegates most output to the delegateComposer, but uses a permutation/selection of fields for the object output. */
public class FoldingByteArrayComposer<E extends Exception> extends FoldingComposer<E> implements BufferedMessageComposer<E> {
    private final BufferedMessageComposer<E> delegateComposer;

    public FoldingByteArrayComposer(BufferedMessageComposer<E> delegateComposer, Map<Class<? extends BonaCustom>, List<String>> mapping, FoldingStrategy errorStrategy) {
        super(delegateComposer, mapping, errorStrategy);
        this.delegateComposer = delegateComposer;
    }
    @Override
    public void reset() {
        delegateComposer.reset();
    }

    @Override
    public int getLength() {
        return delegateComposer.getLength();
    }

    @Override
    public byte[] getBuffer() {
        return delegateComposer.getBuffer();
    }

    @Override
    public byte[] getBytes() {
        return delegateComposer.getBytes();
    }
}
