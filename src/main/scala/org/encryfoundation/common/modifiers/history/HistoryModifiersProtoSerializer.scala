package org.encryfoundation.common.modifiers.history

import BlockProto.BlockProtoMessage.AdProofsProtoMessage
import HeaderProto.HeaderProtoMessage
import PayloadProto.PayloadProtoMessage
import org.encryfoundation.common.modifiers.PersistentModifier
import scala.util.{Failure, Try}

object HistoryModifiersProtoSerializer {

  def toProto(modifier: PersistentModifier): Array[Byte] = modifier match {
    case m: Header   => Header.HeaderTypeId +: HeaderProtoSerializer.toProto(m).toByteArray
    case m: ADProofs => ADProofs.ADProofsTypeId +: ADProofsProtoSerializer.toProto(m).toByteArray
    case m: Payload  => Payload.PayloadTypeId +: PayloadProtoSerializer.toProto(m).toByteArray
    case m           => throw new RuntimeException(s"Try to serialize unknown modifier: $m to proto.")
  }

  def fromProto(bytes: Array[Byte]): Try[PersistentModifier] = Try(bytes.head match {
    case Header.HeaderTypeId     => HeaderProtoSerializer.fromProto(HeaderProtoMessage.parseFrom(bytes.tail))
    case ADProofs.ADProofsTypeId => Try(ADProofsProtoSerializer.fromProto(AdProofsProtoMessage.parseFrom(bytes.tail)))
    case Payload.PayloadTypeId   => PayloadProtoSerializer.fromProto(PayloadProtoMessage.parseFrom(bytes.tail))
    case m                       => Failure(new RuntimeException(s"Try to deserialize unknown modifier: $m from proto."))
  }).flatten
}