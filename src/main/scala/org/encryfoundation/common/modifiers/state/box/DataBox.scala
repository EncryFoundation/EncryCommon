package org.encryfoundation.common.modifiers.state.box

import BoxesProto.BoxProtoMessage
import BoxesProto.BoxProtoMessage.DataBoxProtoMessage
import com.google.common.primitives.{ Bytes, Longs, Shorts }
import com.google.protobuf.ByteString
import io.circe.{ Decoder, Encoder, HCursor }
import io.circe.syntax._
import org.encryfoundation.common.modifiers.state.box.EncryBox.BxTypeId
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.Algos
import org.encryfoundation.prismlang.core.Types
import org.encryfoundation.prismlang.core.wrapped.{ PObject, PValue }
import scala.util.Try

/** Stores arbitrary data in EncryTL binary format. */
case class DataBox(override val proposition: EncryProposition, override val nonce: Long, data: Array[Byte])
    extends EncryBox[EncryProposition] {

  override type M = DataBox

  override val typeId: BxTypeId = DataBox.modifierTypeId

  override def serializer: Serializer[M] = DataBoxSerializer

  override val tpe: Types.Product = Types.DataBox

  override def asVal: PValue = PValue(asPrism, Types.DataBox)

  override def asPrism: PObject = PObject(baseFields ++ Map("data" -> PValue(data, Types.PCollection.ofByte)), tpe)

  override def serializeToProto: BoxProtoMessage = DataBoxProtoSerializer.toProto(this)
}

object DataBox {

  val modifierTypeId: BxTypeId = 4.toByte

  implicit val jsonEncoder: Encoder[DataBox] = (bx: DataBox) =>
    Map(
      "type"        -> modifierTypeId.asJson,
      "id"          -> Algos.encode(bx.id).asJson,
      "proposition" -> bx.proposition.asJson,
      "nonce"       -> bx.nonce.asJson,
      "data"        -> Algos.encode(bx.data).asJson,
    ).asJson

  implicit val jsonDecoder: Decoder[DataBox] = (c: HCursor) =>
    for {
      proposition <- c.downField("proposition").as[EncryProposition]
      nonce       <- c.downField("nonce").as[Long]
      data        <- c.downField("data").as[Array[Byte]]
    } yield
      DataBox(
        proposition,
        nonce,
        data
    )
}

object DataBoxProtoSerializer extends BaseBoxProtoSerialize[DataBox] {

  override def toProto(t: DataBox): BoxProtoMessage =
    BoxProtoMessage().withDataBox(
      DataBoxProtoMessage()
        .withPropositionProtoMessage(ByteString.copyFrom(t.proposition.contractHash))
        .withNonce(t.nonce)
        .withData(ByteString.copyFrom(t.data))
    )

  override def fromProto(b: Array[Byte]): Try[DataBox] = Try {
    val box: BoxProtoMessage = BoxProtoMessage.parseFrom(b)
    DataBox(EncryProposition(box.getDataBox.propositionProtoMessage.toByteArray),
            box.getDataBox.nonce,
            box.getDataBox.data.toByteArray)
  }
}

object DataBoxSerializer extends Serializer[DataBox] {

  override def toBytes(obj: DataBox): Array[Byte] = {
    val propBytes: Array[BxTypeId] = EncryPropositionSerializer.toBytes(obj.proposition)
    Bytes.concat(
      Shorts.toByteArray(propBytes.length.toShort),
      propBytes,
      Longs.toByteArray(obj.nonce),
      Shorts.toByteArray(obj.data.length.toShort),
      obj.data
    )
  }

  override def parseBytes(bytes: Array[Byte]): Try[DataBox] = Try {
    val propositionLen: Short         = Shorts.fromByteArray(bytes.take(2))
    val iBytes: Array[BxTypeId]       = bytes.drop(2)
    val proposition: EncryProposition = EncryPropositionSerializer.parseBytes(iBytes.take(propositionLen)).get
    val nonce: Long                   = Longs.fromByteArray(iBytes.slice(propositionLen, propositionLen + 8))
    val dataLen: Short                = Shorts.fromByteArray(iBytes.slice(propositionLen + 8, propositionLen + 8 + 2))
    val data: Array[BxTypeId]         = iBytes.takeRight(dataLen)
    DataBox(proposition, nonce, data)
  }
}
