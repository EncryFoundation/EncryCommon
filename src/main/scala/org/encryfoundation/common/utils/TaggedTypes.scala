package org.encryfoundation.common.utils

import supertagged.TaggedType

object TaggedTypes {

  type LeafData          = LeafData.Type
  type Side              = Side.Type
  type ADKey             = ADKey.Type
  type ADValue           = ADValue.Type
  type ADDigest          = ADDigest.Type
  type Balance           = Balance.Type
  type ModifierTypeId    = ModifierTypeId.Type
  type ModifierId        = ModifierId.Type
  type Difficulty        = Difficulty.Type
  type Height            = Height.Type

  object LeafData extends TaggedType[Array[Byte]]

  object Side extends TaggedType[Byte]

  object ADKey extends TaggedType[Array[Byte]]

  object ADValue extends TaggedType[Array[Byte]]

  object ADDigest extends TaggedType[Array[Byte]]

  object Balance extends TaggedType[Byte]

  object ModifierTypeId extends TaggedType[Byte]

  object ModifierId extends TaggedType[Array[Byte]]

  object Difficulty extends TaggedType[BigInt]

  object Height extends TaggedType[Int]
}