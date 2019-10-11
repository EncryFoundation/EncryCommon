package org.encryfoundation.common.modifiers.state.box

import org.encryfoundation.common.modifiers.mempool.transaction.Proposition
import org.encryfoundation.common.serialization.BytesSerializable
import org.encryfoundation.common.utils.TaggedTypes.ADKey

trait Box[P <: Proposition] extends BytesSerializable {
  val proposition: P

  val id: ADKey
}

object Box {
  type Amount = Long
}
