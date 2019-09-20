package org.encryfoundation.common.modifiers.history

import HeaderProto.HeaderProtoMessage
import com.google.common.primitives.{Bytes, Ints, Longs}
import com.google.protobuf.ByteString
import org.bouncycastle.crypto.digests.Blake2bDigest
import org.encryfoundation.common.crypto.equihash.{Equihash, EquihashSolution, EquihashSolutionsSerializer}
import org.encryfoundation.common.modifiers.{ModifierWithDigest, PersistentModifier}
import org.encryfoundation.common.serialization.Serializer
import org.encryfoundation.common.utils.TaggedTypes.{Difficulty, ModifierId, ModifierTypeId}
import scorex.crypto.hash.Digest32
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax._
import Header._
import scala.util.Try
import org.encryfoundation.common.utils.constants.TestNetConstants
import org.encryfoundation.common.utils.Algos

case class Header(version: Byte,
                  override val parentId: ModifierId,
                  transactionsRoot: Digest32,
                  timestamp: Long,
                  height: Int,
                  nonce: Long,
                  difficulty: Difficulty,
                  equihashSolution: EquihashSolution,
                  stateRoot: Array[Byte]) extends PersistentModifier {

  override type M = Header

  override val modifierTypeId: ModifierTypeId = Header.modifierTypeId

  lazy val powHash: Digest32 = getPowHash(this)

  lazy val requiredDifficulty: Difficulty = difficulty

  override lazy val id: ModifierId = ModifierId @@ powHash.untag(Digest32)

  lazy val isGenesis: Boolean = height == TestNetConstants.GenesisHeight

  lazy val payloadId: ModifierId =
    ModifierWithDigest.computeId(Payload.modifierTypeId, id, transactionsRoot)

  def toHeaderProto: HeaderProtoMessage = HeaderProtoSerializer.toProto(this)

  def isRelated(mod: PersistentModifier): Boolean = mod match {
    case t: Payload => transactionsRoot.sameElements(t.digest)
    case _ => false
  }

  override def serializer: Serializer[Header] = HeaderSerializer

  override def toString: String = s"Header(id=$encodedId, height=$height, parent=${Algos.encode(parentId)}, " +
    s"version = $version, transactionsRoot = ${Algos.encode(transactionsRoot)}, timestamp = $timestamp, nonce = $nonce," +
    s"difficulty = $difficulty)"
}

object Header {

  val modifierTypeId: ModifierTypeId = ModifierTypeId @@ (101: Byte)

  lazy val GenesisParentId: ModifierId = ModifierId @@ Array.fill(TestNetConstants.DigestLength)(0: Byte)

  implicit val jsonEncoder: Encoder[Header] = (h: Header) => Map(
    "id" -> Algos.encode(h.id).asJson,
    "version" -> h.version.asJson,
    "parentId" -> Algos.encode(h.parentId).asJson,
    "payloadId" -> Algos.encode(h.payloadId).asJson,
    "txRoot" -> Algos.encode(h.transactionsRoot).asJson,
    "nonce" -> h.nonce.asJson,
    "timestamp" -> h.timestamp.asJson,
    "height" -> h.height.asJson,
    "difficulty" -> h.difficulty.toString.asJson,
    "equihashSolution" -> h.equihashSolution.asJson,
    "stateRoot" -> h.stateRoot.asJson,
  ).asJson

  implicit val jsonDecoder: Decoder[Header] = (c: HCursor) => for {
    version <- c.downField("version").as[Byte]
    parentId <- c.downField("parentId").as[String]
    txRoot <- c.downField("txRoot").as[String]
    timestamp <- c.downField("timestamp").as[Long]
    height <- c.downField("height").as[Int]
    nonce <- c.downField("nonce").as[Long]
    difficulty <- c.downField("difficulty").as[BigInt]
    equihashSolution <- c.downField("equihashSolution").as[EquihashSolution]
    stateRoot <- c.downField("stateRoot").as[String]
  } yield Header(
    version,
    ModifierId @@ Algos.decode(parentId).getOrElse(Array.emptyByteArray),
    Digest32 @@ Algos.decode(txRoot).getOrElse(Array.emptyByteArray),
    timestamp,
    height,
    nonce,
    Difficulty @@ difficulty,
    equihashSolution,
    Algos.decode(stateRoot).getOrElse(Array.emptyByteArray)
  )

  def getPowHash(header: Header): Digest32 = {
    val digest: Blake2bDigest = new Blake2bDigest(256)
    val bytes: Array[Byte] = HeaderSerializer.bytesWithoutPow(header)
    digest.update(bytes, 0, bytes.length)
    Equihash.hashNonce(digest, header.nonce)
    Equihash.hashSolution(digest, header.equihashSolution)
    val h: Array[Byte] = new Array[Byte](32)
    digest.doFinal(h, 0)

    val secondDigest: Blake2bDigest = new Blake2bDigest(256)
    secondDigest.update(h, 0, h.length)
    val result: Array[Byte] = new Array[Byte](32)
    secondDigest.doFinal(result, 0)

    Digest32 @@ result
  }
}

object HeaderProtoSerializer {

  //TODO check big int difficulty
  def toProto(header: Header): HeaderProtoMessage = HeaderProtoMessage()
    .withVersion(ByteString.copyFrom(Array(header.version)))
    .withParentId(ByteString.copyFrom(header.parentId))
    .withTransactionsRoot(ByteString.copyFrom(header.transactionsRoot))
    .withTimestamp(header.timestamp)
    .withHeight(header.height)
    .withNonce(header.nonce)
    .withDifficulty(header.difficulty.toLong)
    .withInts(header.equihashSolution.ints)
    .withStateRoot(ByteString.copyFrom(header.stateRoot))

  def fromProto(headerProtoMessage: HeaderProtoMessage): Try[Header] = Try(
    Header(
      headerProtoMessage.version.toByteArray.head,
      ModifierId @@ headerProtoMessage.parentId.toByteArray,
      Digest32 @@ headerProtoMessage.transactionsRoot.toByteArray,
      headerProtoMessage.timestamp,
      headerProtoMessage.height,
      headerProtoMessage.nonce,
      Difficulty @@ BigInt(headerProtoMessage.difficulty),
      EquihashSolution(headerProtoMessage.ints),
      headerProtoMessage.stateRoot.toByteArray
    )
  )
}

object HeaderSerializer extends Serializer[Header] {

  def bytesWithoutPow(h: Header): Array[Byte] =
    Bytes.concat(
      Array(h.version),
      h.parentId,
      h.transactionsRoot,
      Longs.toByteArray(h.timestamp),
      Ints.toByteArray(h.difficulty.toByteArray.length),
      h.difficulty.toByteArray,
      Ints.toByteArray(h.height),
      h.stateRoot
    )

  override def toBytes(obj: Header): Array[Byte] =
    Bytes.concat(
      Array(obj.version),
      obj.parentId,
      obj.transactionsRoot,
      Longs.toByteArray(obj.timestamp),
      Ints.toByteArray(obj.height),
      Longs.toByteArray(obj.nonce),
      Ints.toByteArray(obj.difficulty.toByteArray.length),
      obj.difficulty.toByteArray,
      obj.stateRoot,
      obj.equihashSolution.bytes,
    )

  override def parseBytes(bytes: Array[Byte]): Try[Header] = Try {
    val version: Byte = bytes.head
    val parentId: ModifierId = ModifierId @@ bytes.slice(1, 33)
    val txsRoot: Digest32 = Digest32 @@ bytes.slice(33, 65)
    val timestamp: Long = Longs.fromByteArray(bytes.slice(65, 73))
    val height: Int = Ints.fromByteArray(bytes.slice(73, 77))
    val nonce: Long = Longs.fromByteArray(bytes.slice(77, 85))
    val diificultySize: Int = Ints.fromByteArray(bytes.slice(85, 89))
    val difficulty: Difficulty = Difficulty @@ BigInt(bytes.slice(89, 89 + diificultySize))
    val stateRoot: Array[Byte] = bytes.slice(89 + diificultySize, 89 + diificultySize + TestNetConstants.stateRootSize)
    val equihashSolution: EquihashSolution = EquihashSolutionsSerializer
      .parseBytes(bytes.slice(89 + diificultySize + TestNetConstants.stateRootSize, bytes.length)).get

    Header(version, parentId, txsRoot, timestamp, height, nonce, difficulty, equihashSolution, stateRoot)
  }
}