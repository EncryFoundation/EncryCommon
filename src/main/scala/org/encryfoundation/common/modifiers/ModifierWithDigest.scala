package org.encryfoundation.common.modifiers

import org.encryfoundation.common.utils.TaggedTypes.{ModifierId, ModifierTypeId}
import org.encryfoundation.prismlang.utils.{BouncyCastleHasher, Hasher}

trait ModifierWithDigest extends PersistentNodeViewModifier with BouncyCastleHasher {

  override lazy val id: ModifierId = ModifierId @@ prefixedHash(modifierTypeId, headerId, digest).repr

  def digest: Array[Byte]

  def headerId: Array[Byte]
}