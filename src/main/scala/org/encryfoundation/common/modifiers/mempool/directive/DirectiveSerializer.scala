package org.encryfoundation.common.modifiers.mempool.directive

import TransactionProto.TransactionProtoMessage.DirectiveProtoMessage
import TransactionProto.TransactionProtoMessage.DirectiveProtoMessage.DirectiveProto
import org.encryfoundation.common.serialization.Serializer
import scala.util.{Failure, Try}

trait ProtoDirectiveSerializer[T] {

  def toProto(message: T): DirectiveProtoMessage

  def fromProto(message: DirectiveProtoMessage): Option[T]
}

object DirectiveProtoSerializer {

  def fromProto(message: DirectiveProtoMessage): Option[Directive] = message.directiveProto match {
    case DirectiveProto.AssetIssuingDirectiveProto(_)  => AssetIssuingDirectiveProtoSerializer.fromProto(message)
    case DirectiveProto.DataDirectiveProto(_)          => DataDirectiveProtoSerializer.fromProto(message)
    case DirectiveProto.TransferDirectiveProto(_)      => TransferDirectiveProtoSerializer.fromProto(message)
    case DirectiveProto.ScriptedAssetDirectiveProto(_) => ScriptedAssetDirectiveProtoSerializer.fromProto(message)
    case DirectiveProto.Empty                          => None
  }
}

object DirectiveSerializer extends Serializer[Directive] {

  override def toBytes(obj: Directive): Array[Byte] = obj match {
    case td: TransferDirective       => TransferDirective.TransferDirectiveTypeId +: TransferDirectiveSerializer.toBytes(td)
    case aid: AssetIssuingDirective  => AssetIssuingDirective.AssetIssuingDirectiveTypeId +: AssetIssuingDirectiveSerializer.toBytes(aid)
    case sad: ScriptedAssetDirective => ScriptedAssetDirective.ScriptedAssetDirectiveTypeId +: ScriptedAssetDirectiveSerializer.toBytes(sad)
    case dd: DataDirective           => DataDirective.DataDirectiveTypeId +: DataDirectiveSerializer.toBytes(dd)
    case m                           => throw new Exception(s"Serialization of unknown directive type: $m")
  }

  override def parseBytes(bytes: Array[Byte]): Try[Directive] = Try(bytes.head).flatMap {
    case TransferDirective.TransferDirectiveTypeId           => TransferDirectiveSerializer.parseBytes(bytes.tail)
    case AssetIssuingDirective.AssetIssuingDirectiveTypeId   => AssetIssuingDirectiveSerializer.parseBytes(bytes.tail)
    case ScriptedAssetDirective.ScriptedAssetDirectiveTypeId => ScriptedAssetDirectiveSerializer.parseBytes(bytes.tail)
    case DataDirective.DataDirectiveTypeId                   => DataDirectiveSerializer.parseBytes(bytes.tail)
    case t                                                   => Failure(new Exception(s"Got unknown typeId for directive: $t"))
  }
}