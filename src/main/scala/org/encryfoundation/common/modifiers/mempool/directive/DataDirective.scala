package org.encryfoundation.common.modifiers.mempool.directive

import TransactionProto.TransactionProtoMessage.DirectiveProtoMessage
import TransactionProto.TransactionProtoMessage.DirectiveProtoMessage.DataDirectiveProtoMessage
import com.google.common.primitives.{ Bytes, Ints }
import com.google.protobuf.ByteString
import io.circe.syntax._
import io.circe.{ Decoder, Encoder, HCursor }
import org.encryfoundation.common.modifiers.mempool.directive.Directive.DTypeId
import org.encryfoundation.common.modifiers.state.box.{ DataBox, EncryBaseBox, EncryProposition }
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.constants.TestNetConstants
import org.encryfoundation.common.utils.{ Algos, Utils }
import org.encryfoundation.prismlang.compiler.CompiledContract.ContractHash
import scorex.crypto.hash.Digest32

import scala.util.Try

case class DataDirective(contractHash: ContractHash, data: Array[Byte]) extends Directive {

  override type M = DataDirective

  override val typeId: DTypeId = DataDirective.modifierTypeId

  override lazy val isValid: Boolean = data.length <= TestNetConstants.MaxDataLength

  override def boxes(digest: Digest32, idx: Int): Seq[EncryBaseBox] =
    Seq(DataBox(EncryProposition(contractHash), Utils.nonceFromDigest(digest ++ Ints.toByteArray(idx)), data))

  override def serializer: Serializer[M] = DataDirectiveSerializer

  override def toDirectiveProto: DirectiveProtoMessage = DataDirectiveProtoSerializer.toProto(this)
}

object DataDirective {

  val modifierTypeId: DTypeId = 5: Byte

  implicit val jsonEncoder: Encoder[DataDirective] = (d: DataDirective) =>
    Map(
      "typeId"       -> d.typeId.asJson,
      "contractHash" -> Algos.encode(d.contractHash).asJson,
      "data"         -> Algos.encode(d.data).asJson
    ).asJson

  implicit val jsonDecoder: Decoder[DataDirective] = (c: HCursor) =>
    for {
      contractHash <- c.downField("contractHash").as[String]
      dataEnc      <- c.downField("data").as[String]
    } yield
      Algos
        .decode(contractHash)
        .flatMap(ch => Algos.decode(dataEnc).map(data => DataDirective(ch, data)))
        .getOrElse(throw new Exception("Decoding failed"))
}

object DataDirectiveProtoSerializer extends ProtoDirectiveSerializer[DataDirective] {

  override def toProto(message: DataDirective): DirectiveProtoMessage =
    DirectiveProtoMessage()
      .withDataDirectiveProto(
        DataDirectiveProtoMessage()
          .withContractHash(ByteString.copyFrom(message.contractHash))
          .withData(ByteString.copyFrom(message.data))
      )

  override def fromProto(message: DirectiveProtoMessage): Option[DataDirective] =
    message.directiveProto.dataDirectiveProto match {
      case Some(value) => Some(DataDirective(value.contractHash.toByteArray, value.data.toByteArray))
      case None        => Option.empty[DataDirective]
    }
}

object DataDirectiveSerializer extends Serializer[DataDirective] {

  override def toBytes(obj: DataDirective): Array[Byte] =
    Bytes.concat(
      obj.contractHash,
      Ints.toByteArray(obj.data.length),
      obj.data
    )

  override def parseBytes(bytes: Array[Byte]): Try[DataDirective] = Try {
    val contractHash: ContractHash = bytes.take(TestNetConstants.DigestLength)
    val dataLen: Int               = Ints.fromByteArray(bytes.slice(TestNetConstants.DigestLength, TestNetConstants.DigestLength + 4))
    val data: Array[DTypeId] =
      bytes.slice(TestNetConstants.DigestLength + 4, TestNetConstants.DigestLength + 4 + dataLen)
    DataDirective(contractHash, data)
  }
}
