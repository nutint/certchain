package com.nat.certchain.util

import java.security.MessageDigest
import java.math.BigInteger

class CryptoHash {
  def hash(inputs: List[Any]): String = {
    val stringValue = inputs.map(_.toString).mkString("")
    val digested = MessageDigest.getInstance("SHA-256").digest(stringValue.getBytes("UTF-8"))
    String.format("%32x", new BigInteger(1, digested))
  }
}
