package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import io.findify.flinkpb.Codec.{JavaCodec, ScalaCodec, ScalaOneofCodec, ScalaOptionalOneofCodec}
import io.findify.flinkpb.config.{
  JavaConfigSnapshot,
  ScalaConfigSnapshot,
  ScalaOneofConfigSnapshot,
  ScalaOptionalOneofConfigSnapshot
}
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSchemaCompatibility, TypeSerializerSnapshot}
import org.apache.flink.api.java.typeutils.runtime.{DataInputViewStream, DataOutputViewStream}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

case class ProtobufSerializer[T](codec: Codec[T]) extends TypeSerializer[T] {

  override def isImmutableType: Boolean       = true
  override def duplicate(): TypeSerializer[T] = this
  override def createInstance(): T            = codec.defaultInstance
  override def copy(from: T): T               = from
  override def copy(from: T, reuse: T): T     = from
  override def getLength: Int                 = -1

  override def serialize(record: T, target: DataOutputView): Unit = {
    codec.writeTo(new DataOutputViewStream(target), record)
  }

  override def deserialize(source: DataInputView): T = {
    codec.parseFrom(new DataInputViewStream(source))
  }

  override def deserialize(reuse: T, source: DataInputView): T =
    deserialize(source)

  override def copy(source: DataInputView, target: DataOutputView): Unit =
    serialize(deserialize(source), target)

  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = codec match {
    case s: ScalaCodec[_]         => new ScalaConfigSnapshot(s).asInstanceOf[TypeSerializerSnapshot[T]]
    case j: JavaCodec[_]          => new JavaConfigSnapshot(j).asInstanceOf[TypeSerializerSnapshot[T]]
    case s: ScalaOneofCodec[_, _] => new ScalaOneofConfigSnapshot(s).asInstanceOf[TypeSerializerSnapshot[T]]
    case s: ScalaOptionalOneofCodec[_, _] =>
      new ScalaOptionalOneofConfigSnapshot(s).asInstanceOf[TypeSerializerSnapshot[T]]
  }
}
