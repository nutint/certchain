package com.nat.certchain.config

import com.nat.certchain.blockchain.model.Block

case class BlockchainConfig(
  genesisData: Block,
  mineRate: Long
)
