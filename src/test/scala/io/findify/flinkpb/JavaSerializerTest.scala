package io.findify.flinkpb

import io.findify.flinkprotobuf.java.Tests.Root.Nested
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JavaSerializerTest extends AnyFlatSpec with Matchers with SerializerTest {
  import io.findify.flinkprotobuf.java.Tests._

  it should "write simple messages" in {
    val ser = FlinkProtobuf.generateJava(classOf[Foo], Foo.getDefaultInstance)
    roundtrip(ser, Foo.newBuilder().setValue(1).build())
    serializable(ser)
    snapshotSerializable(ser)
  }

  it should "write nested messages" in {
    val ser = FlinkProtobuf.generateJava(classOf[Root], Root.getDefaultInstance)
    roundtrip(ser, Root.newBuilder().addList(Nested.newBuilder().setValue(1).build()).build())
    serializable(ser)
    snapshotSerializable(ser)
  }

  it should "write oneof messages" in {
    val ser = FlinkProtobuf.generateJava(classOf[Sealed], Sealed.getDefaultInstance)
    roundtrip(ser, Sealed.newBuilder().setFoo(Foo.newBuilder().setValue(1).build()).build())
    serializable(ser)
    snapshotSerializable(ser)
  }
}
