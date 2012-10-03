package de.jpaw.bonaparte.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Wrapper implements BonaPortable, Externalizable {
    private static final long serialVersionUID = 130622384733L;
    private static final ConcurrentMap<String,String> property$Map = new ConcurrentHashMap<>();
    
    public ConcurrentMap<String,String> get$PropertyMap() {
        return property$Map;
    }
    
	public int get$rtti() {
		return 0;
	}
	
    public String get$Property(String id) {
        return property$Map.get(id);
    }
    
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
        return false;
    }
    @Override
    public void validate() throws ObjectValidationException {
        if (data != null)
            data.validate();
    }

    @Override
    public long get$Serial() {
        return serialVersionUID;
    }
}
