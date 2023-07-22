package dev.jombi.diskt

import com.solana.Solana
import com.solana.api.*
import com.solana.core.DerivationPath
import com.solana.core.HotAccount
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.core.TransactionInstruction
import com.solana.models.buffer.AccountInfoData
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.networking.serialization.serializers.base64.BorshAsBase64JsonArraySerializer
import com.solana.programs.AssociatedTokenProgram
import com.solana.programs.TokenProgram
import com.solana.vendor.bip39.Mnemonic
import dev.jombi.diskt.config.Config
import dev.kord.common.entity.optional.value
import dev.kord.gateway.*
import dev.kord.rest.service.RestClient
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import kotlin.io.path.Path
import kotlin.io.path.readText

private val LOGGER = LoggerFactory.getLogger("main")

suspend fun main() {
    val cfg = Json.decodeFromString<Config>(Path("config.json").readText())
    val sol = Solana(HttpNetworkingRouter(RPCEndpoint.MainNetworkBeta))
    val hot = HotAccount.fromMnemonic(cfg.mnemonic.split(' '), "", DerivationPath.BIP44_M_44H_501H_0H)
    val wallet = SolanaWallet(sol)
    val rest = RestClient(cfg.token)
    val gateway = DefaultGateway {}

    val prefix = "\\"

    gateway.on<Ready> {
        LOGGER.info("Account using '{}'.", data.user.username)
    }

    gateway.on<MessageReadConfirm> {
        if (data.ackType.value == 0)
            LOGGER.info("Manually unread from {} which id is {}", data.channelId, data.messageId)
//        else
//            LOGGER.info("Message read confirmed in {} which id is {}", data.channelId, data.messageId)
    }

    gateway.on<MessageCreate> {
        if (message.author.id != rest.user.getCurrentUser().id) return@on
        val content = message.content
        if (!content.startsWith(prefix)) return@on
        val raw = content.drop(prefix.length)
        val cmd = raw.substringBefore(' ')
        val args = raw.split(' ').drop(1)
        if (cmd.startsWith(prefix)) return@on
        rest.channel.deleteMessage(message.channelId, message.id)
        when (cmd.lowercase()) {
            "cur" -> {
                rest.channel.createMessage(message.channelId) {
                    this.content = """
                    |# Current address: '${hot.publicKey}'
                    |```
                    |${wallet.balanceSOL(hot)} SOL
                    |${wallet.balanceUSDT(hot)} USDT
                    |```
                    |``Powered by Solana``""".trimMargin()
                }
            }
        }
    }
    val s = System.currentTimeMillis()
    Runtime.getRuntime().addShutdownHook(Thread {
        LOGGER.info("bruhed with {}ms", System.currentTimeMillis() - s)
    })

    gateway.start(cfg.token)
}