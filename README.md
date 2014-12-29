##Support library for the main Bonaparte DSL

The classes defined within this repository are supporting the generated Java POJOs of the DSL.

The bonaparte-core project contains the essential portion. Here, most of the interfaces and support classes are defined.
There are a couple of sample implementations for serializers and deserializers, for example

* StringBuilderComposer, StringBuilderComposer - Serializer and deserializer into the Bonaportable format (the format which contributed the name), with a StringBuilder as input / output type.
* ByteArrayComposer, ByteArrayParser - Serializer and deserializer into the same Bonaportable format, but which work with a Java byte [] / resp. ByteBuilder utility class, i.e. this includes encoding / decoding.
* CompactByteArrayComposer, CompactByteArrayParser - Serializer and deserializer into a binary (but not Java specific) format, which work with a Java byte [] / resp. ByteBuilder utility class. These classes can either encode class reference information as the PQON string (PQON = partially qualified object name), similar to the Bonaportable format, or use 2 integers, similar to the factoryId / classId used by Hazelcast (the latter requires you to assign such a pair uniquely to every class in scope).

Most of the other projects are either demonstration showcases for integration with other libraries such as netty, Apache Camel etc., or additional serializers (into Excel, using Apache poi / poix, or Android widgets), or adapters showcasing the integration with non Bonaparte classes (projects bonaparte-adapter-*).

Some documentation and a tutorial is available in the projects bonaparte-tex and bonaparte-tutorial. The sample code used in the tutorial sits in bonaparte-tutorial-code.

###Building

This project uses maven3 as a build tool. Just run

    (cd bonaparts-defs && mvn install)
    (cd bonaparts-base && mvn install)

