package com.nat.certchain.blockchain.model

import com.nat.certchain.config.BlockchainConfig
import com.nat.certchain.util.{CryptoHash, DateFactory}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest._

class BlockSpec extends FreeSpec with Matchers {

  "should be success" in {
    assert(true)
  }

  "genesisBlock should be the same as BlockchainConfiguration" in {
    implicit val blockchainConfig = BlockchainConfig(
      Block(0, "lastHash", "hash", "data", 0, 0)
    )

    assert(Block.genesis == blockchainConfig.genesisData)
  }

  "mineBlock" - {
    val lastBlock = Block(0, "lastHash", "hash", "data", 0, 0)
    implicit val dateFactory = new DateFactory
    implicit val cryptoHash = new CryptoHash
    "should have last hash from hash of last block" in {
      assert(Block.mineBlock(lastBlock, "").lastHash == lastBlock.hash)
    }

    "should have the input data" in {
      assert(Block.mineBlock(lastBlock, "newData").data == "newData")
    }

    "should have valid SHA256 as Hash" in {
      assert(Block.mineBlock(lastBlock, "newData").hash == cryptoHash.hash(Nil))
    }
  }
}
