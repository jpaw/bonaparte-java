package de.jpaw.bonaparte.core;


/** Parent class of all MessageComposers. */
public abstract class AbstractMessageComposer<E extends Exception> extends AbstractMessageWriter<E> implements MessageComposer<E> {

    @Override
    public void writeRecord(BonaCustom o) throws E {
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
    }
    
    @Override
    public void writeObject(BonaCustom o) throws E {
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
    }
}
