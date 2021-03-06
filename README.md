# Protobuf serialization support for Apache Flink

[![CI Status](https://github.com/findify/flink-protobuf/workflows/CI/badge.svg)](https://github.com/flink-protobuf/workflows/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.findify/flink-protobuf_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.findify/flink-protobuf_2.12)
[![License: Apache 2](https://img.shields.io/badge/License-Apache2-green.svg)](https://opensource.org/licenses/Apache2.0)

This project is an adapter to connect [Google Protobuf](https://developers.google.com/protocol-buffers) to the flink's 
own `TypeInformation`-based [serialization framework](https://flink.apache.org/news/2020/04/15/flink-serialization-tuning-vol-1.html). 
This project can be useful if you have:
* [oneof-encoded](https://developers.google.com/protocol-buffers/docs/proto#oneof) protobuf messages, 
  which cannot be efficiently encoded using flink's serialization without Kryo fallback.
* flexible requirements on schema evolution for POJO classes (as compared to 
  [Flinks' for POJOs and Scala case classes](https://ci.apache.org/projects/flink/flink-docs-master/docs/dev/datastream/fault-tolerance/schema_evolution/))
* schema evolution support is needed for scala case classes (as Flink lacks it out of the box)

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

## Schema evolution

Compared to Flink schema evolution for POJO classes, with `flink-protobuf` you can do much more:
* fields can be renamed (as protobuf uses an index-based encoding for field names)
* types can be changed (so optional field can be made repeated, or int32 can be upcasted to int64)

For Scala case classes Flink has no support for schema evolution, so with this project you can:
* add, rename, remove fields
* change field types

## Compatibility

The library is built over Flink 1.13 for Scala 2.12, but should be binary compatible with older flink versions.
Scala 2.11 version is not planned, as ScalaPB already dropped it's support.

## License

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/