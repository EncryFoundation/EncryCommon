package org.encryfoundation.common.modifiers.state

import org.encryfoundation.common.modifiers.state.box._
import scala.util.{Failure, Try}

object StateModifierSerializer {

  def parseBytes(bytes: Array[Byte], typeId: Byte): Try[EncryBaseBox] = typeId match {
    case AssetBox.`modifierTypeId`        => AssetBoxSerializer.parseBytes(bytes)
    case TokenIssuingBox.`modifierTypeId` => AssetIssuingBoxSerializer.parseBytes(bytes)
    case DataBox.`modifierTypeId`         => DataBoxSerializer.parseBytes(bytes)
    case t                                => Failure(new Exception(s"Got unknown typeId: $t"))
  }
}