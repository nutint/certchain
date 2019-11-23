package com.nat.certchain.blockchain.model

import com.nat.certchain.config.BlockchainConfig
import com.nat.certchain.util.{CryptoHash, DateFactory}

case class Blockchain(
  chain: List[Block]
) {
  def replaceChain(newChain: List[Block])(implicit bc: BlockchainConfig, cryptoHash: CryptoHash): Blockchain =
    if(newChain.length <= chain.length) this
    else if(Blockchain.isValidChain(newChain)) copy(chain = newChain)
    else this

  def addBlock(data: String)(implicit df: DateFactory, ch: CryptoHash, bc: BlockchainConfig): Blockchain =
    copy(chain = chain :+ Block.mineBlock(chain.reverse.head, data, 0))
}

object Blockchain {
  def apply(implicit bc: BlockchainConfig): Blockchain =
    Blockchain(
      bc.genesisData :: Nil
    )

  def isValidChain(chain: List[Block])(implicit bc: BlockchainConfig, cryptoHash: CryptoHash): Boolean = {
    chain.head == bc.genesisData &&
    chain.tail.foldLeft((true, bc.genesisData.hash)) { (lastCalc, block) =>
      val (wasTrue, lastHash) = lastCalc
      (wasTrue, lastHash == block.lastHash && Block.isValidBlock(block)) match {
        case (false, _) => (false, block.hash)
        case (true, true) => (true, block.hash)
        case (true, false) => (false, block.hash)
      }
    }._1
  }
}
