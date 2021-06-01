package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import io.findify.flinkpb.Codec.{JavaCodec, ScalaCodec}
import org.apache.flink.api.common.typeinfo.TypeInformation
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import scala.reflect.{ClassTag, classTag}

object FlinkProtobuf {
  def generateScala[T <: GeneratedMessage: ClassTag](companion: GeneratedMessageCompanion[T]): TypeInformation[T] =
    new ProtobufTypeInformation[T](ScalaCodec(companion, classTag[T].runtimeClass.asInstanceOf[Class[T]]))

  def generateJava[T <: GeneratedMessageV3](clazz: Class[T], instance: T): TypeInformation[T] =
    new ProtobufTypeInformation[T](JavaCodec(instance, clazz))
}
