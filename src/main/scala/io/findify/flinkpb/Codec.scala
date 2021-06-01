package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import java.io.{DataOutputStream, InputStream, OutputStream}

sealed trait Codec[T] {
  def clazz: Class[T]
  def defaultInstance: T
  def parseFrom(in: InputStream): T
  def writeTo(out: OutputStream, value: T): Unit
}

object Codec {
  case class ScalaCodec[T <: GeneratedMessage](companion: GeneratedMessageCompanion[T], clazz: Class[T])
      extends Codec[T] {
    override def defaultInstance: T = companion.defaultInstance
    override def parseFrom(in: InputStream): T =
      companion.parseDelimitedFrom(in).getOrElse(throw new IllegalArgumentException("cannot parse message"))
    override def writeTo(out: OutputStream, value: T): Unit = value.writeDelimitedTo(out)
  }

  case class JavaCodec[T <: GeneratedMessageV3](singleton: T, clazz: Class[T]) extends Codec[T] {
    override def defaultInstance: T                         = singleton.getDefaultInstanceForType.asInstanceOf[T]
    override def parseFrom(in: InputStream): T              = singleton.getParserForType.parseDelimitedFrom(in).asInstanceOf[T]
    override def writeTo(out: OutputStream, value: T): Unit = value.writeDelimitedTo(out)
  }
}
