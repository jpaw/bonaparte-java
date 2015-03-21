package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;

/** This class represents a bonaportable which is not defined at compile-time but rather at runtime.
 * It can be seen as a key / value map which integrates into existing classes.
 * Care must be taken not to use any static method to determine RTTI or PQON, but rather obtain all information through the instance's BClass. */
public class AbstractDynamicBonaPortable implements BonaPortable {
    private static final long serialVersionUID = -5308388498938365844L;
    private final BonaPortableClass<?> _my$BClass;
    private transient boolean _was$Frozen = false;      // current state of this instance

    public AbstractDynamicBonaPortable(BonaPortableClass<?> bClass) {
        _my$BClass = bClass;
    }

    @Override
    public String ret$PQON() {
        return _my$BClass.getPqon();
    }

    @Override
    public String ret$Parent() {
        return _my$BClass.getParent().getPqon();
    }

    @Override
    public String ret$Bundle() {
        return _my$BClass.getBundle();
    }

    @Override
    public BonaPortableClass<? extends BonaPortable> ret$BonaPortableClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int ret$rtti() {
        return _my$BClass.getRtti();
    }

    @Override
    public <E extends Exception> void serializeSub(MessageComposer<E> w) throws E {
        // TODO Auto-generated method stub

    }

    @Override
    public <E extends Exception> void deserialize(MessageParser<E> p) throws E {
        // TODO Auto-generated method stub

    }

    @Override
    public void validate() throws ObjectValidationException {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends BonaPortable> T copyAs(Class<T> desiredSuperType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E extends Exception> void foldedOutput(MessageComposer<E> w, ParsedFoldingComponent pfc) throws E {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeWalkString(DataConverter<String, AlphanumericElementaryDataItem> _cvt, boolean descend) {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeWalkBigDecimal(DataConverter<BigDecimal, NumericElementaryDataItem> _cvt, boolean descend) {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeWalkObject(DataConverter<Object, FieldDefinition> _cvt, boolean descend) {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeWalkBonaPortable(DataConverter<BonaPortable, ObjectReference> _cvt, boolean descend) {
        // TODO Auto-generated method stub

    }

    @Override
    public ClassDefinition ret$MetaData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void freeze() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean was$Frozen() {
        return _was$Frozen;
    }

    @Override
    public BonaPortable ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) throws ObjectValidationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BonaPortable ret$FrozenClone() throws ObjectValidationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put$Active(boolean _a) throws ObjectValidationException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean ret$Active() {
        // TODO Auto-generated method stub
        return false;
    }

}
