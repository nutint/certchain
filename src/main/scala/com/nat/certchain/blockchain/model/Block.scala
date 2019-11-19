package com.nat.certchain.blockchain.model

import java.util.Date

case class Block(
  timestamp: Date,
  lastHash: String,
  hash: String,
  data: String,
  nonce: Int,
  difficulty: Int
)
