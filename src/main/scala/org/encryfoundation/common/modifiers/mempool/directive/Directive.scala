package org.encryfoundation.common.modifiers.mempool.directive

import TransactionProto.TransactionProtoMessage.DirectiveProtoMessage
import io.circe._
import org.encryfoundation.common.modifiers.mempool.directive.Directive.DTypeId
import org.encryfoundation.common.modifiers.state.box.EncryBaseBox
import org.encryfoundation.common.serialization.BytesSerializable
import scorex.crypto.hash.Digest32

trait Directive extends BytesSerializable {

  val typeId: DTypeId

  val isValid: Boolean

  def boxes(digest: Digest32, idx: Int): Seq[EncryBaseBox]

  def toDirectiveProto: DirectiveProtoMessage
}

object Directive {

  type DTypeId = Byte

  implicit val jsonEncoder: Encoder[Directive] = {
    case td: TransferDirective       => TransferDirective.jsonEncoder(td)
    case aid: AssetIssuingDirective  => AssetIssuingDirective.jsonEncoder(aid)
    case sad: ScriptedAssetDirective => ScriptedAssetDirective.jsonEncoder(sad)
    case dad: DataDirective          => DataDirective.jsonEncoder(dad)
    case _                           => throw new Exception("Incorrect directive type")
  }

  implicit val jsonDecoder: Decoder[Directive] = Decoder.instance(c =>
    c.downField("typeId").as[DTypeId] match {
      case Right(s) => s match {
        case TransferDirective.`modifierTypeId`      => TransferDirective.jsonDecoder(c)
        case AssetIssuingDirective.`modifierTypeId`  => AssetIssuingDirective.jsonDecoder(c)
        case ScriptedAssetDirective.`modifierTypeId` => ScriptedAssetDirective.jsonDecoder(c)
        case DataDirective.`modifierTypeId`          => DataDirective.jsonDecoder(c)
        case _                                       => Left(DecodingFailure("Incorrect directive typeID", c.history))
      }
      case Left(_) => Left(DecodingFailure("None typeId", c.history))
    }
  )
}