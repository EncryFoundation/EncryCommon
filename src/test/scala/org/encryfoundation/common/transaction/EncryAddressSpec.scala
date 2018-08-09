package org.encryfoundation.common.transaction

import org.encryfoundation.common.crypto.encoding.Base58Check
import org.encryfoundation.common.transaction.EncryAddress.Address
import org.scalatest.{Matchers, PropSpec}
import scorex.util.encode.Base58
import scorex.crypto.signatures.{Curve25519, PublicKey}
import scorex.utils.Random

class EncryAddressSpec extends PropSpec with Matchers {

  val pubKey: PublicKey = Curve25519.createKeyPair(Random.randomBytes())._2
  val validP2PK: Address = Base58Check.encode(Pay2PubKeyAddress.TypePrefix +: pubKey)
  val validP2CH: Address = Base58Check.encode(Pay2ContractHashAddress.TypePrefix +: PubKeyLockedContract(pubKey).contract.hash)
  val invalidP2PK: Address = Base58.encode(Random.randomBytes())
  val invalidP2CH: Address = Base58.encode(Random.randomBytes())
  val invalidP2CHPrefix: Address = Base58Check.encode(99.toByte +: Random.randomBytes())
  val p2pk: Pay2PubKeyAddress = Pay2PubKeyAddress(validP2PK)
  val p2ch: Pay2ContractHashAddress = Pay2ContractHashAddress(validP2CH)

  property("Addresses resolving") {

    EncryAddress.resolveAddress(validP2PK).isSuccess shouldBe true

    EncryAddress.resolveAddress(validP2CH).isSuccess shouldBe true

    EncryAddress.resolveAddress(invalidP2PK).isSuccess shouldBe false

    EncryAddress.resolveAddress(invalidP2CH).isSuccess shouldBe false

    EncryAddress.resolveAddress(invalidP2CHPrefix).isSuccess shouldBe false
  }

  property("p2pk to p2ch") {

    p2pk.p2ch shouldEqual p2ch
  }

  property("PubKey extraction") {

    p2pk.pubKey sameElements pubKey shouldBe true
  }

  property("isValid()") {

    Pay2ContractHashAddress(validP2CH).isValid shouldBe true

    Pay2ContractHashAddress(invalidP2CH).isValid shouldBe false

    Pay2PubKeyAddress(validP2PK).isValid shouldBe true

    Pay2PubKeyAddress(invalidP2PK).isValid shouldBe false
  }
}
