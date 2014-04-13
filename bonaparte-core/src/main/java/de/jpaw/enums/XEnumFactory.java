package de.jpaw.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Factory class which returns an XEnum instance for a given token or name.
 * It is not a factory in the classic sense that a new object is created, rather the unique instance for that token is returned.
 * There is one instance of this class per XEnum class. */
public class XEnumFactory<E extends AbstractXEnumBase<E>> {
	private final int maxTokenLength;
	private final String pqon;				// partially qualified class name of the base
	private final Class<E> baseClass;
	private final Map<String,E> tokenToXEnum = new ConcurrentHashMap<String,E>();
	private final Map<String,E> nameToXEnum = new ConcurrentHashMap<String,E>();
	private final Map<Enum<?>,E> baseEnumToXEnum = new ConcurrentHashMap<Enum<?>,E>();
	private static final Map<String, XEnumFactory<?>> registry = new ConcurrentHashMap<String, XEnumFactory<?>>(200);
	
	// TODO: should only be invoked from XEnum classes. How to verify this? (C++ "friend" needed here...)
	public XEnumFactory(int maxTokenLength, Class<E> baseClass, String pqon) {
		this.maxTokenLength = maxTokenLength;
		this.pqon = pqon;
		this.baseClass = baseClass;
		// "this" is not yet fully constructed...
//		if (registry.put(pqon, this) != null) {
//			throw something;
//		};
		// we do it later instead...
	}
	public static final XEnumFactory<?> getByPQON(String pqon) {
		return registry.get(pqon);
	}
	public void publishInstance(E e) {
		if (tokenToXEnum.put(e.getToken(), e) != null)
			throw new IllegalArgumentException(e.getClass().getSimpleName() + ": duplicate token " + e.getToken() + " for base XEnum " + pqon);
		if (nameToXEnum.put(e.name(), e) != null)
			throw new IllegalArgumentException(e.getClass().getSimpleName() + ": duplicate name " + e.name() + " for base XEnum " + pqon);
		baseEnumToXEnum.put(e.getBaseEnum(), e);
	}
	public void register(String thisPqon) {
		registry.put(thisPqon, this);
	}
	
	public Class<E> getBaseClass() {
		return baseClass;
	}
	public E getByToken(String token) {
		return tokenToXEnum.get(token);
	}
	public E getByName(String name) {
		return nameToXEnum.get(name);
	}
	public E getByEnum(Enum<?> enumVal) {
		return baseEnumToXEnum.get(enumVal);
	}
	public int getMaxTokenLength() {
		return maxTokenLength;
	}
	public String getPqon() {
		return pqon;
	}
}
