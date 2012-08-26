package de.jpaw.bonaparte.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Wrapper implements BonaPortable, Externalizable {

    public BonaPortable data;
    
    public BonaPortable getData() {
        return data;
    }
    public void setData(BonaPortable data) {
        this.data = data;
    }
    
    @Override
    public void writeExternal(ObjectOutput _out) throws IOException {
        serializeSub(new ExternalizableComposer(_out));
    }
    
    @Override
    public void readExternal(ObjectInput _in) throws IOException,
            ClassNotFoundException {
        deserialize(new ExternalizableParser(_in));
    }
    
	@Override
	public String get$PQON() {
		return "Wrapper";
	}
	@Override
	public String get$Revision() {
		return null;
	}
	@Override
	public String get$Parent() {
		return null;
	}
	@Override
	public String get$Bundle() {
		return null;
	}
	@Override
	public <E extends Exception> void serializeSub(MessageComposer<E> w)
			throws E {
		w.addField(data);
		w.writeSuperclassSeparator();
	}
	@Override
	public <E extends Exception> void deserialize(MessageParser<E> p) throws E {
		data = p.readObject(BonaPortable.class, true, true);
	}
	@Override
	public boolean hasSameContentsAs(BonaPortable that) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void validate() throws ObjectValidationException {
		if (data != null)
			data.validate();
	}
}
