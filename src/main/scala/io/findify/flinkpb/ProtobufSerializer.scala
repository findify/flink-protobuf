package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import io.findify.flinkpb.Codec.{JavaCodec, ScalaCodec}
import io.findify.flinkpb.ProtobufSerializer.{JavaConfigSnapshot, ScalaConfigSnapshot}
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSchemaCompatibility, TypeSerializerSnapshot}
import org.apache.flink.api.java.typeutils.runtime.{DataInputViewStream, DataOutputViewStream}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}
import org.apache.flink.util.InstantiationUtil
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import scala.reflect.ClassTag

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
    case s: ScalaCodec[_] => new ScalaConfigSnapshot(s).asInstanceOf[TypeSerializerSnapshot[T]]
    case j: JavaCodec[_]  => new JavaConfigSnapshot(j).asInstanceOf[TypeSerializerSnapshot[T]]
  }
}

object ProtobufSerializer {

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
          .resolveClassByName[GeneratedMessageCompanion[T with GeneratedMessage]](in, userCodeClassLoader)
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

  class JavaConfigSnapshot[T <: GeneratedMessageV3]() extends TypeSerializerSnapshot[T] {
    var codec: JavaCodec[T] = _
    def this(c: JavaCodec[T]) = {
      this()
      codec = c
    }

    override def getCurrentVersion: Int = 1

    override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
      codec = JavaCodec(
        parser = InstantiationUtil
          .resolveClassByName[Parser[T]](in, userCodeClassLoader)
          .getDeclaredConstructor()
          .newInstance(),
        clazz = InstantiationUtil.resolveClassByName[T](in, userCodeClassLoader)
      )
    }

    override def writeSnapshot(out: DataOutputView): Unit = {
      out.writeUTF(codec.clazz.getName)
      out.writeUTF(codec.parser.getClass.getName)
    }

    override def restoreSerializer(): TypeSerializer[T] = new ProtobufSerializer[T](codec)

    override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
      TypeSerializerSchemaCompatibility.compatibleAsIs()
  }
}
