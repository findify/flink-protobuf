package io.findify.flinkpb

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JavaSerializerTest extends AnyFlatSpec with Matchers with SerializerTest {
  import io.findify.flinkprotobuf.java.Tests._

  it should "write simple messages" in {
    Foo.getDefaultInstance
    val ser = FlinkProtobuf.generateJava(classOf[Foo], Foo.parser())
    roundtrip(ser, Foo.newBuilder().setValue(1).build())
  }
}
