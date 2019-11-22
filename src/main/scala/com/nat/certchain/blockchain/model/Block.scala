package com.nat.certchain.blockchain.model

import com.nat.certchain.util.{CryptoHash, DateFactory}
import com.nat.certchain.config.BlockchainConfig

import scala.annotation.tailrec

case class Block(
  timestamp: Long,
  lastHash: String,
  hash: String,
  data: String,
  nonce: Int,
  difficulty: Int
) {
  def increaseDifficulty: Block = copy(difficulty = difficulty + 1).sanitizeDifficulty
  def decreaseDifficulty: Block = copy(difficulty = difficulty - 1).sanitizeDifficulty

  def sanitizeDifficulty: Block = if(difficulty < 1) copy(difficulty = 1) else this
}

object Block {
  def genesis(implicit bcConfig: BlockchainConfig): Block = bcConfig.genesisData


  @scala.annotation.tailrec
  def mineBlock(
    lastBlock: Block, data: String, nonce: Int
  )(
    implicit dateFactory: DateFactory, cryptoHash: CryptoHash, bcConfig: BlockchainConfig
  ): Block = {
    val currentTimestamp = dateFactory.getCurrentTimestamp
    val difficulty = adjustDifficulty(lastBlock, currentTimestamp).difficulty
    val hash = cryptoHash.hash(
      List[Any](
        currentTimestamp,
        difficulty,
        data,
        lastBlock.hash,
        nonce).map(_.toString))

    if(validateHashDifficulty(hash, difficulty)) mineBlock(lastBlock, data, nonce+1)
    else Block(currentTimestamp, lastBlock.hash, hash, data, nonce, difficulty)
  }

  def validateHashDifficulty(hashString: String, difficulty: Int): Boolean = {
    hashString.startsWith("0" * 3)
  }

  def adjustDifficulty(originalBlock: Block, timestamp: Long = 0)(implicit bcConfig: BlockchainConfig): Block = {
    if (originalBlock.difficulty < 1) originalBlock.sanitizeDifficulty
    else if (timestamp - originalBlock.timestamp > bcConfig.mineRate) originalBlock.decreaseDifficulty
    else originalBlock.increaseDifficulty
  }
}
