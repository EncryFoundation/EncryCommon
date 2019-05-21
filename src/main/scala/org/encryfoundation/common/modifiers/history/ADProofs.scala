package org.encryfoundation.common.modifiers.history

import BlockProto.BlockProtoMessage.AdProofsProtoMessage
import com.google.common.primitives.Bytes
import com.google.protobuf.ByteString
import encry.modifiers.state.box._
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax._
import org.encryfoundation.common.modifiers.{PersistentModifier, ModifierWithDigest}
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.TaggedTypes._
import org.encryfoundation.common.utils.{Algos, Constants}
import scorex.crypto.authds.avltree.batch.{BatchAVLVerifier, Insert, Modification, Remove}
import scorex.crypto.hash.Digest32

import scala.util.{Failure, Success, Try}

case class ADProofs(headerId: ModifierId, proofBytes: SerializedAdProof)
  extends PersistentModifier with ModifierWithDigest {

  override def digest: Digest32 = ADProofs.proofDigest(proofBytes)

  override val modifierTypeId: ModifierTypeId = ADProofs.modifierTypeId

  override type M = ADProofs

  override lazy val serializer: Serializer[ADProofs] = ADProofSerializer

  override def toString: String = s"ADProofs(${Algos.encode(id)},${Algos.encode(headerId)},${Algos.encode(proofBytes)})"

  def toProtoADProofs: AdProofsProtoMessage = ADProofsProtoSerializer.toProto(this)

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

  override def parseBytes(bytes: Array[Byte]): Try[ADProofs] = Try {
    ADProofs(
      ModifierId @@ bytes.take(Constants.ModifierIdSize),
      SerializedAdProof @@ bytes.slice(Constants.ModifierIdSize, bytes.length))
  }
}