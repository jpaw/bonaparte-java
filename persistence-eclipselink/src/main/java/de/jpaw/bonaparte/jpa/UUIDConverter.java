package de.jpaw.bonaparte.jpa;

import java.sql.Types;
import java.util.UUID;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDConverter extends AbstractConverter implements Converter {
	private static final long serialVersionUID = -1190276099237996872L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UUIDConverter.class);

	@Override
	public UUID convertObjectValueToDataValue(Object objectValue, Session session) {
		return (UUID) objectValue;
	}

	@Override
	public UUID convertDataValueToObjectValue(Object dataValue, Session session) {
		return (UUID) dataValue;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public void initialize(DatabaseMapping mapping, Session session) {
		DatabaseField field = mapping.getField();
		field.setSqlType(Types.OTHER);
		field.setTypeName("java.util.UUID");
		if (isPostgres(session))
			field.setColumnDefinition("UUID");
	}
}
