package io.findify.flinkpb.config

import io.findify.flinkpb.Codec.ScalaCodec
import io.findify.flinkpb.ProtobufSerializer
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSchemaCompatibility, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}
import org.apache.flink.util.InstantiationUtil
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

class ScalaConfigSnapshot[T <: GeneratedMessage]() extends TypeSerializerSnapshot[T] {
  var codec: ScalaCodec[T] = _
  def this(c: ScalaCodec[T]) = {
    this()
    codec = c
  }

  override def getCurrentVersion: Int = 1

  override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
    codec = ScalaCodec(
      companion = InstantiationUtil
        .resolveClassByName[GeneratedMessageCompanion[T]](in, userCodeClassLoader)
        .getField("MODULE$")
        .get(null)
        .asInstanceOf[GeneratedMessageCompanion[T]],
      clazz = InstantiationUtil.resolveClassByName[T](in, userCodeClassLoader)
    )
  }

  override def writeSnapshot(out: DataOutputView): Unit = {
    out.writeUTF(codec.companion.getClass.getName)
    out.writeUTF(codec.clazz.getName)
  }

  override def restoreSerializer(): TypeSerializer[T] = new ProtobufSerializer[T](codec)

  override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
    TypeSerializerSchemaCompatibility.compatibleAsIs()
}
