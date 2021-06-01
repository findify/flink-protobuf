package io.findify.flinkpb.config

import io.findify.flinkpb.Codec.{ScalaCodec, ScalaOneofCodec}
import io.findify.flinkpb.ProtobufSerializer
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSchemaCompatibility, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}
import org.apache.flink.util.InstantiationUtil
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, TypeMapper}

class ScalaOneofConfigSnapshot[T, M <: GeneratedMessage]() extends TypeSerializerSnapshot[T] {
  var codec: ScalaOneofCodec[T, M] = _
  def this(c: ScalaOneofCodec[T, M]) = {
    this()
    codec = c
  }

  override def getCurrentVersion: Int = 1

  override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
    codec = ScalaOneofCodec[T, M](
      companion = InstantiationUtil
        .resolveClassByName[GeneratedMessageCompanion[M]](in, userCodeClassLoader)
        .getField("MODULE$")
        .get(null)
        .asInstanceOf[GeneratedMessageCompanion[M]],
      clazz = InstantiationUtil.resolveClassByName[T](in, userCodeClassLoader),
      mapper = InstantiationUtil
        .resolveClassByName[TypeMapper[M, T]](in, userCodeClassLoader)
        .getDeclaredConstructor()
        .newInstance()
    )
  }

  override def writeSnapshot(out: DataOutputView): Unit = {
    out.writeUTF(codec.companion.getClass.getName)
    out.writeUTF(codec.clazz.getName)
    out.writeUTF(codec.mapper.getClass.getName)
  }

  override def restoreSerializer(): TypeSerializer[T] = new ProtobufSerializer[T](codec)

  override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
    TypeSerializerSchemaCompatibility.compatibleAsIs()
}
