package org.encryfoundation.common.modifiers.history

import HeaderProto.HeaderProtoMessage
import PayloadProto.PayloadProtoMessage
import org.encryfoundation.common.modifiers.PersistentModifier
import scala.util.{Failure, Try}

object HistoryModifiersProtoSerializer {

  def toProto(modifier: PersistentModifier): Array[Byte] = modifier match {
    case m: Header   => Header.modifierTypeId +: HeaderProtoSerializer.toProto(m).toByteArray
    case m: Payload  => Payload.modifierTypeId +: PayloadProtoSerializer.toProto(m).toByteArray
    case m           => throw new RuntimeException(s"Try to serialize unknown modifier: $m to proto.")
  }

  def fromProto(bytes: Array[Byte]): Try[PersistentModifier] = Try(bytes.head match {
    case Header.`modifierTypeId`   => HeaderProtoSerializer.fromProto(HeaderProtoMessage.parseFrom(bytes.tail))
    case Payload.`modifierTypeId`  => PayloadProtoSerializer.fromProto(PayloadProtoMessage.parseFrom(bytes.tail))
    case m                         => Failure(new RuntimeException(s"Try to deserialize unknown modifier: $m from proto."))
  }).flatten
}