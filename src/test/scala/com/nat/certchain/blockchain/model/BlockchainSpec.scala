package com.nat.certchain.blockchain.model

import com.nat.certchain.config.BlockchainConfig
import com.nat.certchain.util.{CryptoHash, DateFactory}
import org.scalatest._

class BlockchainSpec extends FreeSpec with Matchers {

  implicit val blockchainConfig = BlockchainConfig(
    Block(0, "lastHash", "hash", "genesis", 0, 0),
    1000
  )
  implicit val dateFactory = new DateFactory
  implicit val cryptoHash = new CryptoHash
  "Blockchain" - {
    def blockchain = Blockchain(blockchainConfig)
    "should start with genesis block" in {
      blockchain.chain.headOption shouldBe Some(blockchainConfig.genesisData)
    }

    "adding new block should work correctly" in {
      val newBlockchain: Blockchain = blockchain.addBlock("new data")
      newBlockchain.chain.reverse.headOption.map(_.data) shouldBe Some("new data")
    }

    "isValidChain()" - {
      "when the chain is not start with genesis block should be error" in {
        val nonGenesisBlock = blockchainConfig.genesisData.copy(hash = "invalid hash")
        val invalidGenesisBlockChain = Blockchain(nonGenesisBlock :: Nil)
        Blockchain.isValidChain(invalidGenesisBlockChain.chain) shouldBe false
      }

      "when starts with genesis block and has multiple blocks" - {
        def multipleBlocksInChains = blockchain
          .addBlock("1st block")
          .addBlock("2nd block")
          .addBlock(("3rd block"))

        "and last hash reference has changed" in {
          val invalidHashBlockchain = multipleBlocksInChains
            .copy(chain = multipleBlocksInChains
              .chain
              .map(block => if(block!==blockchainConfig.genesisData) block.copy(lastHash = "fakeLast Hash") else block))
          Blockchain.isValidChain(invalidHashBlockchain.chain) shouldBe false
        }

        "and the chain data is tempered" in {
          val invalidDataBlockchain = multipleBlocksInChains
            .copy(chain =
              multipleBlocksInChains
                .chain
                .map(block => if(block!==blockchainConfig.genesisData) block.copy(data = "fake fake data haha") else block))
          Blockchain.isValidChain(invalidDataBlockchain.chain) shouldBe false
        }
      }
    }
  }
}
