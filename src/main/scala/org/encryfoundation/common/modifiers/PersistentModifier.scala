package org.encryfoundation.common.modifiers

import org.encryfoundation.common.utils.TaggedTypes.ModifierId

trait PersistentModifier extends PersistentNodeViewModifier {

  override def parentId: ModifierId
}