package org.encryfoundation.common.utils.constants

import org.encryfoundation.common.utils.TaggedTypes.ADKey

trait Constants {

  val DigestLength: Int

  val ModifierIdSize: Int

  // Maximum block payload size in bytes
  val PayloadMaxSize: Int

  // Maximum block header size in bytes
  val HeaderMaxSize: Int

  val DefaultKeepVersions: Int

  val PersistentByteCost: Int

  val StateByteCost: Int

  val MaxDataLength: Int

  val AfterGenesisStateDigestHex: String

  val GenesisStateVersion: String

  val IntrinsicTokenId: ADKey
}