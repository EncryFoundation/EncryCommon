package org.encryfoundation.common.utils.constants

import org.encryfoundation.common.utils.TaggedTypes.{ ADKey, Difficulty, Height }
import scala.concurrent.duration.FiniteDuration

trait Constants {

  val DigestLength: Int

  val ModifierIdSize: Int

  // Maximum block payload size in bytes
  val PayloadMaxSize: Int

  // Maximum block header size in bytes
  val HeaderMaxSize: Int

  val stateRootSize: Int

  val DefaultKeepVersions: Int

  val PersistentByteCost: Int

  val StateByteCost: Int

  val MaxDataLength: Int

  val AfterGenesisStateDigestHex: String

  val GenesisStateVersion: String

  val IntrinsicTokenId: ADKey

  val ConsensusScheme: String

  val HashLength: Int

  val MaxTarget: BigInt

  val InitialDifficulty: Difficulty

  val Version: Byte

  val InitialEmissionAmount: Int

  val EmissionDecay: Double

  val EmissionEpochLength: Int

  // Desired time interval between blocks
  val DesiredBlockInterval: FiniteDuration

  val NewHeaderTimeMultiplier: Int

  // Number of last epochs for difficulty recalculation
  val RetargetingEpochsQty: Int

  val EpochLength: Int

  val GenesisHeight: Height

  val PreGenesisHeight: Height

  // Maximum number of epochs blockchain state can be rolled back
  val MaxRollbackDepth: Int
  // creationHeight must be bigger than maxVersions
  val SnapshotCreationHeight: Int

  // Maximum delta any timestamp can differ from current estimated time
  val MaxTimeDrift: Long

  val n: Char

  val k: Char
}
