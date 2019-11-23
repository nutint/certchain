package com.nat.certchain.util

import java.security.MessageDigest
import java.math.BigInteger

class CryptoHash {
  def hash(inputs: List[String]): String = {
    val stringValue = inputs.sorted.mkString("")
    val digested = MessageDigest.getInstance("SHA-256").digest(stringValue.getBytes("UTF-8"))
    String.format("%32x", new BigInteger(1, digested))
  }
}
