package org.encryfoundation.common.utils

import org.encryfoundation.common.utils.TaggedTypes.{ADKey, Height}

object Constants {

  val ModifierIdSize: Int = 32

  val DigestLength: Int = 32

  // Maximum block payload size in bytes
  val PayloadMaxSize: Int = 1000000

  val MaxDataLength: Int = 1000

  val IntrinsicTokenId: ADKey = ADKey !@@ Algos.hash("intrinsic_token")

  object Chain {

    val HashLength: Int = 32

    val GenesisHeight: Height = Height @@ 0
  }
}
