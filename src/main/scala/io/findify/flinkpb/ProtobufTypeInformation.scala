package io.findify.flinkpb

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.{ClassTag, classTag}

case class ProtobufTypeInformation[T](codec: Codec[T]) extends TypeInformation[T] {
  override def isBasicType: Boolean                                         = false
  override def isTupleType: Boolean                                         = false
  override def getArity: Int                                                = 1
  override def getTotalFields: Int                                          = 1
  override def getTypeClass: Class[T]                                       = codec.clazz
  override def isKeyType: Boolean                                           = false
  override def createSerializer(config: ExecutionConfig): TypeSerializer[T] = new ProtobufSerializer[T](codec)
  override def canEqual(obj: Any): Boolean                                  = codec.clazz.isInstance(obj)
}
