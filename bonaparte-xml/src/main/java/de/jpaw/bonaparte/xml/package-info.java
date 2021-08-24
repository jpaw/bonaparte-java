// This source has been automatically created by the bonaparte DSL. Do not modify, changes will be lost.
// The bonaparte DSL is open source, licensed under Apache License, Version 2.0. It is based on Eclipse Xtext2.
// The sources for bonaparte-DSL can be obtained at www.github.com/jpaw/bonaparte-dsl.git

@XmlSchema(namespace = "http://www.jpaw.de/schema/bonaparte.xsd", elementFormDefault = XmlNsForm.QUALIFIED, xmlns = { @XmlNs(prefix="bon", namespaceURI="http://www.jpaw.de/schema/bonaparte.xsd") })

@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=LocalDate.class,       value=LocalDateAdapter.class),
    @XmlJavaTypeAdapter(type=LocalTime.class,       value=LocalTimeAdapter.class),
    @XmlJavaTypeAdapter(type=LocalDateTime.class,   value=LocalDateTimeAdapter.class),
    @XmlJavaTypeAdapter(type=Instant.class,         value=InstantAdapter.class),
    @XmlJavaTypeAdapter(type=ByteArray.class,       value=ByteArrayAdapter.class)
})
package de.jpaw.bonaparte.xml;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import de.jpaw.util.ByteArray;
import de.jpaw.xml.jaxb.ByteArrayAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Instant;
import de.jpaw.xml.jaxb.InstantAdapter;
import de.jpaw.xml.jaxb.LocalDateAdapter;
import de.jpaw.xml.jaxb.LocalTimeAdapter;
import de.jpaw.xml.jaxb.LocalDateTimeAdapter;
