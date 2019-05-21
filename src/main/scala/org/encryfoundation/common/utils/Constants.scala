package org.encryfoundation.common.utils

import org.encryfoundation.common.utils.TaggedTypes.Height

object Constants {

  val ModifierIdSize: Int = 32

  val DigestLength: Int = 32

  // Maximum block payload size in bytes
  val PayloadMaxSize: Int = 1000000

  object Chain {

    val HashLength: Int = 32

    val GenesisHeight: Height = Height @@ 0
  }
}
