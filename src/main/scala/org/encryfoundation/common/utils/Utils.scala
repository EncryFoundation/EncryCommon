package org.encryfoundation.common.utils

import java.nio.{ ByteBuffer, ByteOrder }
import com.google.common.primitives.Longs
import org.bouncycastle.crypto.Digest

object Utils {

  private val byteSize = 8

  def leIntToByteArray(i: Int): Array[Byte] = {
    val buffer: ByteBuffer = ByteBuffer.allocate(Integer.SIZE / 8)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(i)
    buffer.array
  }

  def nonceToLeBytes(nonce: BigInt): Array[Byte] =
    (for (i <- 0 to 7) yield leIntToByteArray((nonce >> 32 * i).intValue())).reduce(_ ++ _)

  def hashNonce[T <: Digest](digest: T, nonce: BigInt): T = {
    val arr = nonceToLeBytes(nonce)
    digest.update(arr, 0, arr.length)
    digest
  }

  def countLeadingZeroes(bytes: Array[Byte]): Byte =
    (0 until byteSize * bytes.length).foldLeft(0.toByte) {
      case (res, i) if (bytes(i / byteSize) << i % byteSize & 0x80) == 0 => (res + 1).toByte
      case (res, _) => return res
    }

  def validateSolution(solution: Array[Byte], target: Double): Boolean = countLeadingZeroes(solution) >= target

  def nonceFromDigest(digest: Array[Byte]): Long = Longs.fromByteArray(Algos.hash(digest).take(8))
}
