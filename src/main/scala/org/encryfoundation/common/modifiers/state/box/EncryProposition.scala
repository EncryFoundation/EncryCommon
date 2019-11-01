package org.encryfoundation.common.modifiers.state.box

import io.circe.syntax._
import io.circe.{ Decoder, Encoder, HCursor }
import org.encryfoundation.common.modifiers.mempool.transaction.EncryAddress.Address
import org.encryfoundation.common.modifiers.mempool.transaction._
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.Algos
import org.encryfoundation.common.utils.TaggedTypes.Height
import org.encryfoundation.common.utils.constants.TestNetConstants
import org.encryfoundation.prismlang.compiler.CompiledContract.ContractHash
import scorex.crypto.signatures.PublicKey

import scala.util.{ Failure, Success, Try }

case class EncryProposition(contractHash: ContractHash) extends Proposition {

  override type M = EncryProposition

  override def serializer: Serializer[EncryProposition] = EncryPropositionSerializer
}

object EncryProposition {

  case object UnlockFailedException extends Exception("Unlock failed")

  implicit val jsonEncoder: Encoder[EncryProposition] = (p: EncryProposition) =>
    Map(
      "contractHash" -> Algos.encode(p.contractHash).asJson
    ).asJson

  implicit val jsonDecoder: Decoder[EncryProposition] = (c: HCursor) =>
    for { contractHash <- c.downField("contractHash").as[String] } yield
      EncryProposition(Algos.decode(contractHash).getOrElse(Array.emptyByteArray))

  def open: EncryProposition                            = EncryProposition(OpenContract.contract.hash)
  def heightLocked(height: Height): EncryProposition    = EncryProposition(HeightLockedContract(height).contract.hash)
  def pubKeyLocked(pubKey: PublicKey): EncryProposition = EncryProposition(PubKeyLockedContract(pubKey).contract.hash)
  def addressLocked(address: Address): EncryProposition =
    EncryAddress
      .resolveAddress(address)
      .map {
        case p2pk: Pay2PubKeyAddress       => EncryProposition(PubKeyLockedContract(p2pk.pubKey).contract.hash)
        case p2sh: Pay2ContractHashAddress => EncryProposition(p2sh.contractHash)
      }
      .getOrElse(throw EncryAddress.InvalidAddressException)
}

object EncryPropositionSerializer extends Serializer[EncryProposition] {

  override def toBytes(obj: EncryProposition): Array[Byte] = obj.contractHash

  override def parseBytes(bytes: Array[Byte]): Try[EncryProposition] =
    if (bytes.lengthCompare(TestNetConstants.DigestLength) == 0) Success(EncryProposition(bytes))
    else Failure(new Exception("Invalid contract hash length"))
}
