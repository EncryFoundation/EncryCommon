package org.encryfoundation.common.utils

import supertagged.TaggedType

object TaggedTypes {
  type LeafData = LeafData.Type
  type Side = Side.Type
  type ADKey = ADKey.Type
  type ADValue = ADValue.Type
  type ADDigest = ADDigest.Type
  type SerializedAdProof = SerializedAdProof.Type
  type Balance = Balance.Type

  object LeafData extends TaggedType[Array[Byte]]

  object Side extends TaggedType[Byte]

  object ADKey extends TaggedType[Array[Byte]]

  object ADValue extends TaggedType[Array[Byte]]

  object ADDigest extends TaggedType[Array[Byte]]

  object SerializedAdProof extends TaggedType[Array[Byte]]

  object Balance extends TaggedType[Byte]

}
