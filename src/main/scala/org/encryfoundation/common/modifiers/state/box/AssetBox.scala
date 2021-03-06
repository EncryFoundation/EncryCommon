package org.encryfoundation.common.modifiers.state.box

import BoxesProto.BoxProtoMessage
import BoxesProto.BoxProtoMessage.{ AssetBoxProtoMessage, TokenIdProto }
import com.google.common.primitives.{ Bytes, Longs, Shorts }
import com.google.protobuf.ByteString
import io.circe.{ Decoder, Encoder, HCursor }
import io.circe.syntax._
import org.encryfoundation.common.modifiers.state.box.Box.Amount
import org.encryfoundation.common.modifiers.state.box.EncryBox.BxTypeId
import org.encryfoundation.common.modifiers.state.box.TokenIssuingBox.TokenId
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.constants.TestNetConstants
import org.encryfoundation.common.utils.Algos
import org.encryfoundation.prismlang.core.Types
import org.encryfoundation.prismlang.core.wrapped.{ PObject, PValue }

import scala.util.Try

/** Represents monetary asset of some type locked with some `proposition`.
 * `tokenIdOpt = None` if the asset is of intrinsic type. */
case class AssetBox(override val proposition: EncryProposition,
                    override val nonce: Long,
                    override val amount: Amount,
                    tokenIdOpt: Option[TokenId] = None)
    extends EncryBox[EncryProposition]
    with MonetaryBox {

  override type M = AssetBox

  override val typeId: BxTypeId = AssetBox.modifierTypeId

  override def serializer: Serializer[M] = AssetBoxSerializer

  override val tpe: Types.Product = Types.AssetBox

  override def asVal: PValue = PValue(asPrism, Types.AssetBox)

  override def asPrism: PObject =
    PObject(
      baseFields ++ Map(
        "amount"  -> PValue(amount, Types.PInt),
        "tokenId" -> PValue(tokenIdOpt.getOrElse(TestNetConstants.IntrinsicTokenId), Types.PCollection.ofByte)
      ),
      tpe
    )

  override def serializeToProto: BoxProtoMessage = AssetBoxProtoSerializer.toProto(this)
}

object AssetBox {

  val modifierTypeId: BxTypeId = 1.toByte

  implicit val jsonEncoder: Encoder[AssetBox] = (bx: AssetBox) =>
    Map(
      "type"        -> modifierTypeId.asJson,
      "id"          -> Algos.encode(bx.id).asJson,
      "proposition" -> bx.proposition.asJson,
      "nonce"       -> bx.nonce.asJson,
      "value"       -> bx.amount.asJson,
      "tokenId"     -> bx.tokenIdOpt.map(id => Algos.encode(id)).asJson
    ).asJson

  implicit val jsonDecoder: Decoder[AssetBox] = (c: HCursor) =>
    for {
      proposition <- c.downField("proposition").as[EncryProposition]
      nonce       <- c.downField("nonce").as[Long]
      amount      <- c.downField("value").as[Long]
      tokenIdOpt  <- c.downField("tokenId").as[Option[String]]
    } yield
      AssetBox(proposition, nonce, amount, tokenIdOpt.map(str => Algos.decode(str).getOrElse(Array.emptyByteArray)))
}

object AssetBoxProtoSerializer extends BaseBoxProtoSerialize[AssetBox] {

  override def toProto(t: AssetBox): BoxProtoMessage = {
    val initialBox: AssetBoxProtoMessage = AssetBoxProtoMessage()
      .withAmount(t.amount)
      .withPropositionProtoMessage(ByteString.copyFrom(t.proposition.contractHash))
      .withNonce(t.nonce)
    val resultedBox: AssetBoxProtoMessage = t.tokenIdOpt match {
      case Some(value) => initialBox.withTokenId(TokenIdProto().withTokenId(ByteString.copyFrom(value)))
      case _           => initialBox
    }
    BoxProtoMessage().withAssetBox(resultedBox)
  }

  override def fromProto(b: Array[Byte]): Try[AssetBox] = Try {
    val box: BoxProtoMessage = BoxProtoMessage.parseFrom(b)
    AssetBox(
      EncryProposition(box.getAssetBox.propositionProtoMessage.toByteArray),
      box.getAssetBox.nonce,
      box.getAssetBox.amount,
      box.getAssetBox.tokenId.map(_.tokenId.toByteArray)
    )
  }
}

object AssetBoxSerializer extends Serializer[AssetBox] {

  override def toBytes(obj: AssetBox): Array[Byte] = {
    val propBytes = EncryPropositionSerializer.toBytes(obj.proposition)
    Bytes.concat(
      Shorts.toByteArray(propBytes.length.toShort),
      propBytes,
      Longs.toByteArray(obj.nonce),
      Longs.toByteArray(obj.amount),
      obj.tokenIdOpt.getOrElse(Array.empty)
    )
  }

  override def parseBytes(bytes: Array[Byte]): Try[AssetBox] = Try {
    val propositionLen: Short         = Shorts.fromByteArray(bytes.take(2))
    val iBytes: Array[BxTypeId]       = bytes.drop(2)
    val proposition: EncryProposition = EncryPropositionSerializer.parseBytes(iBytes.take(propositionLen)).get
    val nonce: Amount                 = Longs.fromByteArray(iBytes.slice(propositionLen, propositionLen + 8))
    val amount: Amount                = Longs.fromByteArray(iBytes.slice(propositionLen + 8, propositionLen + 8 + 8))
    val tokenIdOpt: Option[TokenId] =
      if ((iBytes.length - (propositionLen + 8 + 8)) == TestNetConstants.ModifierIdSize) {
        Some(iBytes.takeRight(TestNetConstants.ModifierIdSize))
      } else None
    AssetBox(proposition, nonce, amount, tokenIdOpt)
  }
}
