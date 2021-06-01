package io.findify.flinkpb

import com.google.protobuf.{GeneratedMessageV3, Parser}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, TypeMapper}

import java.io.{DataOutputStream, InputStream, OutputStream}

sealed trait Codec[T] {
  def clazz: Class[T]
  def defaultInstance: T
  def parseFrom(in: InputStream): T
  def writeTo(out: OutputStream, value: T): Unit
}

object Codec {
  case class ScalaOptionalOneofCodec[T, M <: GeneratedMessage](
      mapper: TypeMapper[M, Option[T]],
      companion: GeneratedMessageCompanion[M],
      clazz: Class[T]
  ) extends Codec[T] {
    override def defaultInstance: T = mapper
      .toCustom(companion.defaultInstance)
      .getOrElse(throw new IllegalArgumentException("cannot decode empty message"))
    override def parseFrom(in: InputStream): T = {
      val message =
        companion.parseDelimitedFrom(in).getOrElse(throw new IllegalArgumentException("cannot parse message"))
      mapper.toCustom(message).getOrElse(throw new IllegalArgumentException("cannot decode empty message"))
    }

    override def writeTo(out: OutputStream, value: T): Unit = {
      mapper.toBase(Some(value)).writeDelimitedTo(out)
    }
  }
  case class ScalaOneofCodec[T, M <: GeneratedMessage](
      mapper: TypeMapper[M, T],
      companion: GeneratedMessageCompanion[M],
      clazz: Class[T]
  ) extends Codec[T] {
    override def defaultInstance: T = mapper.toCustom(companion.defaultInstance)
    override def parseFrom(in: InputStream): T = {
      val message =
        companion.parseDelimitedFrom(in).getOrElse(throw new IllegalArgumentException("cannot parse message"))
      mapper.toCustom(message)
    }

    override def writeTo(out: OutputStream, value: T): Unit = {
      mapper.toBase(value).writeDelimitedTo(out)
    }
  }

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
