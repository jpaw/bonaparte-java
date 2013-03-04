package de.jpaw.bonaparte.jpa;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.util.ByteArray;

public class BonaPortableUserType implements UserType {

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        byte[] bytes = rs.getBytes(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        ByteArrayParser parser = new ByteArrayParser((byte[]) bytes, 0, -1);
        try {
            return parser.readObject(BonaPortable.class, true, true);
        } catch (MessageParserException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARBINARY);
        } else {
            ByteArrayComposer composer = new ByteArrayComposer();
            composer.addField((BonaPortable) value);
            st.setBytes(index, composer.getBytes());
        }
    }

    @Override
    public Class<ByteArray> returnedClass() {
        return ByteArray.class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARBINARY };
    }

    // common implementation
    // http://blog.xebia.com/2009/11/09/understanding-and-writing-hibernate-user-types/

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public boolean equals(Object object1, Object object2) throws HibernateException {
        if (object1 == object2) {
            return true;
        }
        if ((object1 == null) || (object2 == null)) {
            return false;
        }
        return object1.equals(object2);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

}
