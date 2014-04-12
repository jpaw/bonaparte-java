package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;

public class EnumAsTokenComposerFilter<E extends Exception> extends DelegatingBaseComposer<E> {
    
    public EnumAsTokenComposerFilter(MessageComposer<E> delegateComposer) {
        super(delegateComposer);
    }

    // enums replaced by the internal token
    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws E {
        delegateComposer.addField(StaticMeta.ENUM_TOKEN, n == null ? null : n.name());   // toString == name for this one
    }

    // enum with alphanumeric expansion: delegate to Null/String
    // use the existing token meta here, because the name is better, depsite the length may be too short
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws E {
        delegateComposer.addField(token, n == null ? null : n.toString());  // toString == name for this one
    }

    // xenum with alphanumeric expansion: delegate to Null/String
	@Override
	public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws E {
        delegateComposer.addField(token, n == null ? null : n.name());
	}
}
