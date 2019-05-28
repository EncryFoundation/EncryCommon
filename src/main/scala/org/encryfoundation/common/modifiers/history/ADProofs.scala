package org.encryfoundation.common.modifiers.history

import BlockProto.BlockProtoMessage.AdProofsProtoMessage
import com.google.common.primitives.Bytes
import com.google.protobuf.ByteString
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax._
import org.encryfoundation.common.modifiers.{ModifierWithDigest, PersistentModifier}
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.TaggedTypes._
import org.encryfoundation.common.utils.Algos
import org.encryfoundation.common.utils.constants.TestNetConstants
import scorex.crypto.hash.Digest32
import scala.util.Try

case class ADProofs(headerId: ModifierId,
                    proofBytes: SerializedAdProof) extends PersistentModifier with ModifierWithDigest {

  override type M = ADProofs

  override val modifierTypeId: ModifierTypeId = ADProofs.modifierTypeId

  override lazy val serializer: Serializer[ADProofs] = ADProofSerializer

  override def digest: Digest32 = ADProofs.proofDigest(proofBytes)

  override def toString: String = s"ADProofs(${Algos.encode(id)}, ${Algos.encode(headerId)}, ${Algos.encode(proofBytes)})"

  def toProtoADProofs: AdProofsProtoMessage = ADProofsProtoSerializer.toProto(this)

  override def parentId: ModifierId = null
}

object ADProofs {

  val modifierTypeId: ModifierTypeId = ModifierTypeId @@ (104: Byte)

  implicit val jsonEncoder: Encoder[ADProofs] = (p: ADProofs) => Map(
    "headerId"   -> Algos.encode(p.headerId).asJson,
    "proofBytes" -> Algos.encode(p.proofBytes).asJson,
    "digest"     -> Algos.encode(p.digest).asJson
  ).asJson

  implicit val jsonDecoder: Decoder[ADProofs] = (c: HCursor) => for {
    headerIdAd <- c.downField("headerId").as[String]
    proofBytes <- c.downField("proofBytes").as[String]
  } yield ADProofs(
    ModifierId @@ Algos.decode(headerIdAd).getOrElse(Array.emptyByteArray),
    SerializedAdProof @@ Algos.decode(proofBytes).getOrElse(Array.emptyByteArray),
  )

  def proofDigest(proofBytes: SerializedAdProof): Digest32 = Algos.hash(proofBytes)
}

object ADProofsProtoSerializer {

  def toProto(adProofs: ADProofs): AdProofsProtoMessage = AdProofsProtoMessage()
    .withHeaderId(ByteString.copyFrom(adProofs.headerId))
    .withProofBytes(ByteString.copyFrom(adProofs.proofBytes))


  def fromProto(message: AdProofsProtoMessage): ADProofs = ADProofs(
    ModifierId @@ message.headerId.toByteArray,
    SerializedAdProof @@ message.proofBytes.toByteArray
  )
}

object ADProofSerializer extends Serializer[ADProofs] {

  override def toBytes(obj: ADProofs): Array[Byte] = Bytes.concat(obj.headerId, obj.proofBytes)

  override def parseBytes(bytes: Array[Byte]): Try[ADProofs] = Try(ADProofs(
    ModifierId @@ bytes.take(TestNetConstants.ModifierIdSize),
    SerializedAdProof @@ bytes.slice(TestNetConstants.ModifierIdSize, bytes.length))
  )
}