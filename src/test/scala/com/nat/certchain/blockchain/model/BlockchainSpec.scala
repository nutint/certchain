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

    def multipleBlocksInChains = blockchain
      .addBlock("1st block")
      .addBlock("2nd block")
      .addBlock("3rd block")

    "isValidChain()" - {
      "when the chain is not start with genesis block should be error" in {
        val nonGenesisBlock = blockchainConfig.genesisData.copy(hash = "invalid hash")
        val invalidGenesisBlockChain = Blockchain(nonGenesisBlock :: Nil)
        Blockchain.isValidChain(invalidGenesisBlockChain.chain) shouldBe false
      }

      "when starts with genesis block and has multiple blocks" - {

        "and last hash reference has changed" in {
          val invalidHashBlockchain = multipleBlocksInChains
            .copy(chain = multipleBlocksInChains
              .chain
              .map(block => if(block!==blockchainConfig.genesisData) block.copy(lastHash = "fakeLast Hash") else block))
          Blockchain.isValidChain(invalidHashBlockchain.chain) shouldBe  false
        }

        "and the chain data is tempered" in {
          val invalidDataBlockchain = multipleBlocksInChains
            .copy(chain =
              multipleBlocksInChains
                .chain
                .map(block => if(block!==blockchainConfig.genesisData) block.copy(data = "fake fake data haha") else block))
          Blockchain.isValidChain(invalidDataBlockchain.chain) shouldBe false
        }

        "and the chain is all correct" in {
          Blockchain.isValidChain(multipleBlocksInChains.chain) shouldBe true
        }
      }
    }

    "replaceChain()" - {
      "should not replace when the chain is not longer" in {
        val originalBlockChain = multipleBlocksInChains
        val currentChain = originalBlockChain.chain
        originalBlockChain.replaceChain(Blockchain(blockchainConfig).chain).chain shouldBe currentChain
      }

      "should not replace if the target chain is not pass validation" in {
        val blockChain = multipleBlocksInChains
        val currentChain = blockChain.chain
        val fakeDataChain = blockChain.addBlock("anotherBlock").chain.map(_.copy(data = "fakeData"))
        blockChain.replaceChain(fakeDataChain).chain shouldBe currentChain
      }

      "should replace when new chain is pass validation, and longer then current chain" in {
        val blockChain = multipleBlocksInChains
        val newBlockChain = blockChain.addBlock("anotherBlock")
        blockChain.replaceChain(newBlockChain.chain).chain shouldBe newBlockChain.chain
      }
    }
  }
}
