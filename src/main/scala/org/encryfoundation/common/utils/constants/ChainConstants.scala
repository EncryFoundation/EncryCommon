package org.encryfoundation.common.utils.constants

import org.encryfoundation.common.utils.TaggedTypes.{Difficulty, Height}

import scala.concurrent.duration.FiniteDuration

trait ChainConstants {

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

  // Maximum delta any timestamp can differ from current estimated time
  val MaxTimeDrift: Long
}