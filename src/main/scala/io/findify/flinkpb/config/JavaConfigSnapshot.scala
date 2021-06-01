package io.findify.flinkpb.config

import com.google.protobuf.GeneratedMessageV3
import io.findify.flinkpb.Codec.JavaCodec
import io.findify.flinkpb.ProtobufSerializer
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSchemaCompatibility, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}
import org.apache.flink.util.InstantiationUtil

class JavaConfigSnapshot[T <: GeneratedMessageV3]() extends TypeSerializerSnapshot[T] {
  var codec: JavaCodec[T] = _
  def this(c: JavaCodec[T]) = {
    this()
    codec = c
  }

  override def getCurrentVersion: Int = 1

  override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
    val clazz       = InstantiationUtil.resolveClassByName[T](in, userCodeClassLoader)
    val constructor = clazz.getDeclaredConstructor()
    constructor.setAccessible(true)
    codec = JavaCodec(constructor.newInstance(), clazz)
  }

  override def writeSnapshot(out: DataOutputView): Unit = {
    out.writeUTF(codec.clazz.getName)
  }

  override def restoreSerializer(): TypeSerializer[T] = new ProtobufSerializer[T](codec)

  override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
    TypeSerializerSchemaCompatibility.compatibleAsIs()
}
