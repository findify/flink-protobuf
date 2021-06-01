# Protobuf serialization support for Apache Flink

[![CI Status](https://github.com/findify/flink-protobuf/workflows/CI/badge.svg)](https://github.com/flink-protobuf/workflows/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.findify/flink-adt_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.metarank/cfor_2.13)
[![License: Apache 2](https://img.shields.io/badge/License-Apache2-green.svg)](https://opensource.org/licenses/Apache2.0)

This project is an adapter to connect Google Protobuf to the flink's own `TypeInformation`-based serialization 
framework. This project can be useful if you have:
* oneof-encoded protobuf messages, which cannot be efficiently encoded using flink's serialization without Kryo fallback
* stronger requirements on schema evolution (as compared to Flinks' for POJOs and Scala case classes)

## Usage

`flink-protobuf` is released to Maven-central. For SBT, add this snippet to `build.sbt`:
```scala
libraryDependencies += "io.findify" %% "flink-protobuf" % "0.2"
```
Then, given that you have a following message format:
```proto
message Foo {
    required int32 value = 1;
}
```
You can build a `TypeInformation` for scalapb-generated classes like this:
```scala
import io.findify.flinkpb.FlinkProtobuf

implicit val ti = FlinkProtobuf.generateScala(Foo)
val result      = env.fromCollection(List(Foo(1), Foo(2), Foo(3)))
```

For Java it's going to look a bit different:
```java
import io.findify.flinkprotobuf.java.Tests;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

TypeInformation<Tests.Foo> ti = FlinkProtobuf.generateJava(Tests.Foo.class, Tests.Foo.getDefaultInstance());
env.fromCollection(List.of(Tests.Foo.newBuilder().setValue(1).build()), ti).executeAndCollect(100);

```

## Compatibility

The library is built over Flink 1.13 for both Scala 2.11 and 2.12, but should be binary compatible with older flink versions.

## License

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/