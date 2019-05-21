package org.encryfoundation.common.modifiers

import org.encryfoundation.common.utils.Algos
import org.encryfoundation.common.utils.TaggedTypes.{ModifierId, ModifierTypeId}

trait ModifierWithDigest extends PersistentNodeViewModifier {

  override lazy val id: ModifierId = ModifierWithDigest.computeId(modifierTypeId, headerId, digest)

  def digest: Array[Byte]

  def headerId: Array[Byte]
}

object ModifierWithDigest {
  def computeId(modifierType: ModifierTypeId, headerId: Array[Byte], digest: Array[Byte]): ModifierId =
    ModifierId @@ Algos.hash.prefixedHash(modifierType, headerId, digest).repr
}
