package io.findify.flinkpb

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.Suite
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectOutputStream}

trait SerializerTest { this: Suite with Matchers =>
  def roundtrip[T](ti: TypeInformation[T], value: T) = {
    val serializer = ti.createSerializer(new ExecutionConfig())
    val buffer     = new ByteArrayOutputStream()
    serializer.serialize(value, new DataOutputViewStreamWrapper(buffer))
    val decoded = serializer.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(buffer.toByteArray)))
    value shouldBe decoded
  }

  def serializable[T](ti: TypeInformation[T]) = {
    val stream = new ObjectOutputStream(new ByteArrayOutputStream())
    stream.writeObject(ti.createSerializer(new ExecutionConfig()))
  }

}
