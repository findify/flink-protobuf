package io.findify.flinkpb

import io.findify.flinkprotobuf.scala.NonSealedOneof.Whatever.Xfoo
import io.findify.flinkprotobuf.scala.Root.Nested
import io.findify.flinkprotobuf.scala.{
  Bar,
  Bar1,
  Bar2,
  Foo,
  Foo1,
  Foo2,
  NonSealedOneof,
  Root,
  SealedNonOpt,
  SealedNonOptMessage,
  SealedOptional,
  SealedOptionalMessage
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaSerializerTest extends AnyFlatSpec with Matchers with SerializerTest {
  it should "write simple scala types" in {
    val ser = FlinkProtobuf.generateScala(Foo)
    roundtrip(ser, Foo(1))
    serializable(ser)
  }

  it should "write nested messages" in {
    val ser = FlinkProtobuf.generateScala(Root)
    roundtrip(ser, Root(List(Nested(1))))
    serializable(ser)
  }

  it should "write sealed optional oneof messages" in {
    val ser = FlinkProtobuf.generateScala(SealedOptionalMessage)
    roundtrip[SealedOptionalMessage](ser, Foo1(1).asMessage)
    roundtrip[SealedOptionalMessage](ser, Bar1("a").asMessage)
    serializable(ser)
    snapshotSerializable(ser)
  }

  it should "write sealed optional oneof messages via generateScalaOneof" in {
    val ser =
      FlinkProtobuf.generateScalaOptionalOneof[SealedOptional, SealedOptionalMessage](SealedOptionalMessage)
    roundtrip(ser, Foo1(1))
    roundtrip(ser, Bar1("a"))
    serializable(ser)
    snapshotSerializable(ser)
  }

  it should "write sealed nonopt oneof messages" in {
    val ser = FlinkProtobuf.generateScalaOneof[SealedNonOpt, SealedNonOptMessage](SealedNonOptMessage)
    roundtrip[SealedNonOpt](ser, Bar2("a"))
    serializable(ser)
    snapshotSerializable(ser)
  }

  it should "write nonsealed oneof messages" in {
    val ser = FlinkProtobuf.generateScala(NonSealedOneof)
    roundtrip[NonSealedOneof](ser, NonSealedOneof.of(Xfoo(Foo2(1))))
    serializable(ser)
    snapshotSerializable(ser)
  }

}
