package scalapb

import com.google.protobuf.ByteString
import com.google.protobuf.wrappers.{
  BoolValue,
  BytesValue,
  DoubleValue,
  FloatValue,
  Int32Value,
  Int64Value,
  StringValue,
  UInt32Value,
  UInt64Value
}

abstract class TypeMapper[BaseType, CustomType] extends Serializable {
  def toCustom(base: BaseType): CustomType
  def toBase(custom: CustomType): BaseType

  def map2[Other](f: CustomType => Other)(g: Other => CustomType) =
    TypeMapper[BaseType, Other](f.compose(toCustom))((toBase _).compose(g))
}

object TypeMapper {
  def apply[BaseType, CustomType](baseToCustom: BaseType => CustomType)(
      customToBase: CustomType => BaseType
  ): TypeMapper[BaseType, CustomType] = new TypeMapper[BaseType, CustomType] {
    def toCustom(base: BaseType): CustomType = baseToCustom(base)
    def toBase(custom: CustomType): BaseType = customToBase(custom)
  }

  implicit val DoubleValueTypeMapper: TypeMapper[DoubleValue, Double] =
    TypeMapper[DoubleValue, Double](_.value)(com.google.protobuf.wrappers.DoubleValue.apply(_))
  implicit val FloatValueTypeMapper: TypeMapper[FloatValue, Float] =
    TypeMapper[FloatValue, Float](_.value)(com.google.protobuf.wrappers.FloatValue.apply(_))
  implicit val Int64ValueTypeMapper: TypeMapper[Int64Value, Long] =
    TypeMapper[Int64Value, Long](_.value)(com.google.protobuf.wrappers.Int64Value.apply(_))
  implicit val UInt64ValueTypeMapper: TypeMapper[UInt64Value, Long] =
    TypeMapper[UInt64Value, Long](_.value)(com.google.protobuf.wrappers.UInt64Value.apply(_))
  implicit val Int32ValueTypeMapper: TypeMapper[Int32Value, Int] =
    TypeMapper[Int32Value, Int](_.value)(com.google.protobuf.wrappers.Int32Value.apply(_))
  implicit val UInt32ValueTypeMapper: TypeMapper[UInt32Value, Int] =
    TypeMapper[UInt32Value, Int](_.value)(com.google.protobuf.wrappers.UInt32Value.apply(_))
  implicit val BoolValueTypeMapper: TypeMapper[BoolValue, Boolean] =
    TypeMapper[BoolValue, Boolean](_.value)(com.google.protobuf.wrappers.BoolValue.apply(_))
  implicit val StringValueTypeMapper: TypeMapper[StringValue, String] =
    TypeMapper[StringValue, String](_.value)(com.google.protobuf.wrappers.StringValue.apply(_))
  implicit val BytesValueTypeMapper: TypeMapper[BytesValue, ByteString] =
    TypeMapper[BytesValue, ByteString](_.value)(com.google.protobuf.wrappers.BytesValue.apply(_))
}
