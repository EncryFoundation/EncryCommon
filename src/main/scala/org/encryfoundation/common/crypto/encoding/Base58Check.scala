package org.encryfoundation.common.crypto.encoding

import java.util
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake2b256

import scala.util.{ Failure, Success, Try }

object Base58Check {

  val Version: Byte       = 1
  val ChecksumLength: Int = 4

  private def getChecksum(bytes: Array[Byte]): Array[Byte] =
    util.Arrays.copyOfRange(Blake2b256.hash(bytes), 0, ChecksumLength)

  def encode(input: Array[Byte]): String = Base58.encode((Version +: input) ++ getChecksum(input))

  def decode(input: String): Try[Array[Byte]] = Base58.decode(input).flatMap { bytes =>
    val checksum: Array[Byte]       = util.Arrays.copyOfRange(bytes, bytes.length - ChecksumLength, bytes.length)
    val checksumActual: Array[Byte] = getChecksum(util.Arrays.copyOfRange(bytes, 1, bytes.length - ChecksumLength))

    if (checksum.sameElements(checksumActual)) Success(util.Arrays.copyOfRange(bytes, 1, bytes.length - ChecksumLength))
    else Failure(new Exception("Wrong checksum"))
  }
}
