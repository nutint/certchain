package com.nat.certchain.blockchain.model

import com.nat.certchain.config.BlockchainConfig
import com.nat.certchain.util.{CryptoHash, DateFactory}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest._

class BlockSpec extends FreeSpec with Matchers {

  implicit val blockchainConfig = BlockchainConfig(
    Block(0, "lastHash", "hash", "data", 0, 0),
    1000
  )

  "should be success" in {
    assert(true)
  }

  "genesisBlock should be the same as BlockchainConfiguration" in {
    assert(Block.genesis == blockchainConfig.genesisData)
  }

  "mineBlock" - {
    val lastBlock = Block(0, "lastHash", "hash", "data", 0, 0)
    implicit val dateFactory = new DateFactory
    implicit val cryptoHash = new CryptoHash
    "should have last hash from hash of last block" in {
      assert(Block.mineBlock(lastBlock, "", 0).lastHash == lastBlock.hash)
    }

    "should have the input data" in {
      assert(Block.mineBlock(lastBlock, "newData", 0).data == "newData")
    }

    "should have valid SHA256 as Hash" in {
      val minedBlock = Block.mineBlock(lastBlock, "newData", 0)
      assert(
        minedBlock.hash ==
        cryptoHash.hash(
          List(
            minedBlock.timestamp,
            minedBlock.nonce,
            minedBlock.difficulty,
            lastBlock.hash,
            minedBlock.data).map(_.toString)
        )
      )
    }
  }

  "validateHashDifficulty" - {
    "should preceed with 000 when difficulty is 3" in {
      Block.validateHashDifficulty("000abc", 3) shouldBe true
    }

    "should proceed with 0000 when difficulty is 4" in {
      Block.validateHashDifficulty("0000def", 4) shouldBe true
    }

    "should returns false when input 0ab and difficulty is 3" in {
      Block.validateHashDifficulty("0ab", 3) shouldBe false
    }
  }

  "adjustDifficulty" - {
    val block = Block(1000, "last-hash", "hash", "data", 13, 5)
    "should increase when current mine rate is lower then target mine rate" in {
      Block.adjustDifficulty(block, block.timestamp + blockchainConfig.mineRate + 100).difficulty shouldBe(block.difficulty - 1)
    }

    "should decrease when current mine rate is higher than target mine rate" in {
      Block.adjustDifficulty(block, block.timestamp + blockchainConfig.mineRate - 100).difficulty shouldBe(block.difficulty + 1)
    }

    "should not lower than 1" in {
      val minus1BlockDifficulty = block.copy(difficulty = -1)
      Block.adjustDifficulty(minus1BlockDifficulty).difficulty shouldBe 1
    }
  }
}
