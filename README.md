## Support library for the main Bonaparte DSL

The classes defined within this repository are supporting the generated Java POJOs of the DSL.

The bonaparte-core project contains the essential portion. Here, most of the interfaces and support classes are defined.
There are a couple of sample implementations for serializers and deserializers, for example

* StringBuilderComposer, StringBuilderComposer - Serializer and deserializer into the Bonaportable format (the format which contributed the name), with a StringBuilder as input / output type.
* ByteArrayComposer, ByteArrayParser - Serializer and deserializer into the same Bonaportable format, but which work with a Java byte [] / resp. ByteBuilder utility class, i.e. this includes encoding / decoding.
* CompactByteArrayComposer, CompactByteArrayParser - Serializer and deserializer into a binary (but not Java specific) format, which work with a Java byte [] / resp. ByteBuilder utility class. These classes can either encode class reference information as the PQON string (PQON = partially qualified object name), similar to the Bonaportable format, or use 2 integers, similar to the factoryId / classId used by Hazelcast (the latter requires you to assign such a pair uniquely to every class in scope).

Most of the other projects are either demonstration showcases for integration with other libraries such as netty, Apache Camel etc., or additional serializers (into Excel, using Apache poi / poix, or Android widgets), or adapters showcasing the integration with non Bonaparte classes (projects bonaparte-adapter-*).

Some documentation and a tutorial is available in the projects bonaparte-tex and bonaparte-tutorial. The sample code used in the tutorial sits in bonaparte-tutorial-code.

You can find the PDFs at the following URLs:

* [https://github.com/jpaw/bonaparte-java/blob/master/bonaparte-tutorial/tutorial.pdf](https://github.com/jpaw/bonaparte-java/blob/master/bonaparte-tutorial/tutorial.pdf)

* [https://github.com/jpaw/bonaparte-java/blob/master/bonaparte-tex/bonaparte.pdf](https://github.com/jpaw/bonaparte-java/blob/master/bonaparte-tex/bonaparte.pdf)

### Building

This project uses maven3 as a build tool. Just run

    mvn clean install

### Compatibility

There have been some smaller DSL grammar changes, as well as API changes between the generated code and the support library. Normally, updating the support library and the the DSL synchronously should hide those differences, and only implementors of new serialization formats should notice this.

The Bonaparte serialization format is very stable, even across major releases.
Data written into "Bonaportable" format by any release starting with Bonaparte 1.7.12 can be parsed by any release afterwards.
The reverse is not true, due to the addition of new data types (Time, Instant, enum sets) etc. in new releases.

## Support library for the Bonaparte persistence JPA DSL

The classes defined within this repository are supporting the generated JPA entity classes of the DSL.

The projects have the following contents:
  * persistence-base:           Parent project, initiates the build of the child projects.
  * persistence-bom:            Bill of materials / dependency management. Import this into other projects.
  * persistence-core:           Defines annotations and interfaces used by the generated code. JPA 2.1 AttributeConverter classes.
  * persistence-postgres:       Defines Postgres specific mappings for the "jsonb" data type (work in progress)
  * persistence-eclipselink:    Session customizers for Eclipselink (work in progress for update to Eclipselink 2.7.0)
  * persistence-hibernate:      Customizer for Hibernate 5.2.x, for planned support of jsonb Postgres data type (normally not required)

### Building

This project uses maven3 as a build tool. Just run

```sh
    mvn clean install
```

or just
```sh
    ./build.sh
```

### Dependencies

Release 5.0.x switches from Hibernate 5 to Hibernate 6 (jakarta namespace).

For EclipseLink, there are some remaining issues:
* Eclipselink seems to have dropped support for Postgres UUID (this worked out of the box in some prior release)
* For converters to Strings, the @Lob annotation seems to be discarded
* There is some exception at startup about a missing JNDI context (we should not need any), even if NoServerPlatform is specified in the persistence.xml

The converter classes can be listed in persistence.xml as follows:
```xml
        <class>de.jpaw.bonaparte.jpa.converters.ConverterByteArray</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterCompactBonaPortable</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterInstant</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterLocalDate</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterLocalTime</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterLocalDateTime</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterCompactJsonArray</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterCompactJsonElement</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterCompactJsonObject</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterStringJsonArray</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterStringJsonElement</class>
        <class>de.jpaw.bonaparte.jpa.converters.ConverterStringJsonObject</class>
        <class>de.jpaw.bonaparte.jpa.postgres.ConverterNativeJsonArray</class>
        <class>de.jpaw.bonaparte.jpa.postgres.ConverterNativeJsonElement</class>
        <class>de.jpaw.bonaparte.jpa.postgres.ConverterNativeJsonObject</class>
```

