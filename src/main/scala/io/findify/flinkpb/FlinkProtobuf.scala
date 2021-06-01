package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import io.findify.flinkpb.Codec.{JavaCodec, ScalaCodec, ScalaOneofCodec, ScalaOptionalOneofCodec}
import org.apache.flink.api.common.typeinfo.TypeInformation
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, GeneratedSealedOneof, TypeMapper}

import scala.reflect.{ClassTag, classTag}

object FlinkProtobuf {

  def generateScalaOptionalOneof[T: ClassTag, M <: GeneratedMessage](
      companion: GeneratedMessageCompanion[M]
  )(implicit mapper: TypeMapper[M, Option[T]]): TypeInformation[T] = {
    new ProtobufTypeInformation[T](
      ScalaOptionalOneofCodec(mapper, companion, classTag[T].runtimeClass.asInstanceOf[Class[T]])
    )
  }
  def generateScalaOneof[T: ClassTag, M <: GeneratedMessage](
      companion: GeneratedMessageCompanion[M]
  )(implicit mapper: TypeMapper[M, T]): TypeInformation[T] = {
    new ProtobufTypeInformation[T](ScalaOneofCodec(mapper, companion, classTag[T].runtimeClass.asInstanceOf[Class[T]]))
  }

  def generateScala[T <: GeneratedMessage: ClassTag](companion: GeneratedMessageCompanion[T]): TypeInformation[T] =
    new ProtobufTypeInformation[T](ScalaCodec(companion, classTag[T].runtimeClass.asInstanceOf[Class[T]]))

  def generateJava[T <: GeneratedMessageV3](clazz: Class[T], instance: T): TypeInformation[T] =
    new ProtobufTypeInformation[T](JavaCodec(instance, clazz))
}
