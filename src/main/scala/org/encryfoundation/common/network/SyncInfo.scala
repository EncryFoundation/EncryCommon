package org.encryfoundation.common.network

import org.encryfoundation.common.modifiers.history.Header
import org.encryfoundation.common.utils.TaggedTypes.{ModifierId, ModifierTypeId}

case class SyncInfo(lastHeaderIds: Seq[ModifierId]) {

  def startingPoints: Seq[(ModifierTypeId, ModifierId)] = lastHeaderIds.map(id => Header.HeaderTypeId -> id)
}