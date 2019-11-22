package com.nat.certchain.util

import org.scalatest.{FreeSpec, Matchers}

class CryptoHashSpec extends FreeSpec with Matchers {

  val cryptoHash = new CryptoHash
  "hash should returns the expected result" in {
    cryptoHash.hash(List("some string")) shouldBe "61d034473102d7dac305902770471fd50f4c5b26f6831a56dd90b5184b3c30fc"
  }
}