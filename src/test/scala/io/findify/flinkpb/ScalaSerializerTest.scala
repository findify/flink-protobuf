package io.findify.flinkpb

import io.findify.flinkprotobuf.scala.Root.Nested
import io.findify.flinkprotobuf.scala.{Bar, Foo, Root, Sealed, SealedMessage}
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

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

  it should "write oneof messages" in {
    val ser = FlinkProtobuf.generateScala(SealedMessage)
    roundtrip[SealedMessage](ser, Foo(1).asMessage)
    roundtrip[SealedMessage](ser, Bar("a").asMessage)
    serializable(ser)
  }

}
