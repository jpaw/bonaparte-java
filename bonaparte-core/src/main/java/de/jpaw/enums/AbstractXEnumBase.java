package de.jpaw.enums;

/** An instance of an XEnum. */

public abstract class AbstractXEnumBase<E extends AbstractXEnumBase<E>> implements XEnum<E>, Comparable<E> {
    private final Enum<?> _enum;
    private final int _ordinal;
    private final String _name;
    private final String _token;
    private final XEnumFactory<E> _factory;
    
    protected AbstractXEnumBase(Enum<?> enumVal, int ordinal, String name, String token, XEnumFactory<E> factory) {
        this._enum = enumVal;
        this._ordinal = ordinal;
        this._name = name;
        this._token = token;
        this._factory = factory;
    }
    
    @Override
    public final int ordinal() {
        return _ordinal;
    }
    @Override
    public final String name() {
        return _name;
    }
    @Override
    public final String getToken() {
        return _token;
    }
    @Override
    public Enum<?> getBaseEnum() {
        return _enum;
    }
    @Override
    public final String toString() {
        // delegate method to underlying enum and use that method (and any possible override there)
        return _enum.toString();
    }
    @Override
    public final Class<? extends E> getDeclaringClass() {
        return _factory.getBaseClass();  // same as rootclass?
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    protected final void finalize() {
    }

    @Override
    public int compareTo(E o) {
        if (_factory.getBaseClass() != o.getFactory().getBaseClass())
            throw new ClassCastException("Comparing XEnum of base class " + _factory.getBaseClass().getSimpleName() + " with " + o.getFactory().getBaseClass().getSimpleName());
        return Integer.valueOf(_ordinal).compareTo(o.ordinal());
    }

    @Override
    public XEnumFactory<E> getFactory() {
        return _factory;
    }
    
    // special hashCode implementation which is compatible with the underlying enum's implementation
    @Override
    public int hashCode() {
        return _enum.hashCode();
    }
    // special equals implementation which is compatible with the underlying enum's implementation
    // it is not symmetric for the special case of comparing xenum with enum
    @Override
    public boolean equals(Object _o) {
        if (_o == null)
            return false;
        if (_o instanceof Enum) {
            return this._enum.equals(_o);       // special case, comparing an xenum to an enum.
        }
        // must be xenums, or the comparion os false
        return this.getClass() == _o.getClass() && this == _o;  // both have a singleton instance per value
    }
}
