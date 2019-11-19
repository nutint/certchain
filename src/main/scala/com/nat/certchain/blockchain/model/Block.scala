package com.nat.certchain.blockchain.model

import com.nat.certchain.util.{CryptoHash, DateFactory}
import com.nat.certchain.config.BlockchainConfig

case class Block(
  timestamp: Long,
  lastHash: String,
  hash: String,
  data: String,
  nonce: Int,
  difficulty: Int
)

object Block {
  def genesis(implicit bcConfig: BlockchainConfig): Block = bcConfig.genesisData

  def mineBlock(lastBlock: Block, data: String)(implicit dateFactory: DateFactory, cryptoHash: CryptoHash): Block =
    Block(
      dateFactory.getCurrentTimestamp,
      lastBlock.hash,
      cryptoHash.hash(Nil),
      data,
      0,
      0
    )

  def adjustDifficulty(originalBlock: Block, timestamp: Long): Int = 0
}
