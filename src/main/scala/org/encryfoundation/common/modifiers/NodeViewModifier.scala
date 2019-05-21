package org.encryfoundation.common.modifiers

import org.encryfoundation.common.serialization.BytesSerializable
import org.encryfoundation.common.utils.Algos
import org.encryfoundation.common.utils.TaggedTypes.{ModifierId, ModifierTypeId}

trait NodeViewModifier extends BytesSerializable {

  val modifierTypeId: ModifierTypeId

  def id: ModifierId

  def encodedId: String = Algos.encode(id)

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: NodeViewModifier => that.id.sameElements(id) && (that.modifierTypeId == modifierTypeId)
    case _                      => false
  }
}

object NodeViewModifier {

  private val DefaultIdSize: Int = 32

  val ModifierIdSize: Int = DefaultIdSize
}

trait PersistentNodeViewModifier extends NodeViewModifier {
  def parentId: ModifierId
}