package dev.jombi.diskt

import com.solana.Solana
import com.solana.core.DerivationPath
import com.solana.core.HotAccount
import com.solana.core.PublicKey
import com.solana.networking.Commitment
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.networking.socket.SolanaSocketEventsDelegate
import com.solana.networking.socket.models.LogsNotification
import com.solana.networking.socket.models.SocketResponse
import dev.jombi.diskt.config.Config
import dev.jombi.diskt.ff.FFApi
import dev.jombi.diskt.ff.data.Direction
import dev.jombi.diskt.ff.data.SwapType
import dev.jombi.diskt.ff.impl.CreateRequest
import dev.jombi.diskt.ff.impl.PriceRequest
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.optional.value
import dev.kord.gateway.*
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.roundToLong
import kotlin.random.Random

private val LOGGER = LoggerFactory.getLogger("main")
private val whitelist = listOf("290045190926761995", "954910339315679242", "1031427558023118889")

@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    val cfg = Json.decodeFromString<Config>(Path("config.json").readText())
    val sol = Solana(HttpNetworkingRouter(RPCEndpoint.MainNetworkBeta))
    val hot = HotAccount.fromMnemonic(cfg.mnemonic.split(' '), "", DerivationPath.BIP44_M_44H_501H_0H)
    val wallet = SolanaWallet(sol)
    val rest = RestClient(cfg.token)
    val senderRest = RestClient(cfg.sender)
    val channelGot = senderRest.user.createDM(DMCreateRequest(rest.user.getCurrentUser().id))
    val gateway = DefaultGateway {}
    val ff = FFApi(cfg.ffApiKey, cfg.ffApiSecret)

    val delegate = object : SolanaSocketEventsDelegate() {
        private var money = runBlocking { wallet.balanceUSDT(hot)!! }
        override fun logsNotification(notification: SocketResponse<LogsNotification>) {
            runBlocking {
                val prev = money
                money = wallet.balanceUSDT(hot)!!
                if (prev != money) senderRest.channel.createMessage(channelGot.id) {
                    content = if (money > prev) "Received ${money - prev} USDT! (Current: $money USDT)"
                    else "Spent ${money - prev} USDT. (Current: $money USDT)"
                }
            }
        }

        override fun subscribed(socketId: Int, id: String) {
            LOGGER.info("[SUBSCRIBE]")
        }

        override fun start() {
            runBlocking {
                sol.socket.logsSubscribe(listOf(hot.publicKey.toString()), Commitment.FINALIZED).getOrThrow()
                money = wallet.balanceUSDT(hot)!!
            }
        }

        override fun stop() {

        }

        override fun error(error: Exception) {
            LOGGER.error("E", error)
        }
    }
    sol.socket.start(delegate)

    val prefix = "\\"

    gateway.on<Ready> {
        LOGGER.info("Account using '{}'.", data.user.username)
    }

    gateway.on<MessageReadConfirm> {
        if (data.ackType.value == 0)
            LOGGER.info("Manually unread from {} which id is {}", data.channelId, data.messageId)
    }

    gateway.on<MessageCreate> {
        if (message.author.id != rest.user.getCurrentUser().id) return@on
        val content = message.content
        if (message.type != MessageType.Reply)
            return@on
        val address = message.referencedMessage.value!!.content

        val cmd = content.substringBefore(' ')
        val args = content.split(' ').drop(1)
        when (cmd.lowercase()) {
            "ltc" -> {
                val amt = minOf(maxOf(args[0].toDoubleOrNull() ?: return@on, 1.5), wallet.balanceUSDT(hot)!!)
                val p = ff.price(PriceRequest(SwapType.FLOAT, "LTC", "USDTSOL", Direction.TO, 1.0))
                val created = ff.create(
                    CreateRequest(
                        SwapType.FLOAT,
                        "USDTSOL",
                        "LTC",
                        Direction.TO,
                        p.data.from.min.toDouble(),
                        address
                    )
                )

                val t = wallet.sendSPL(hot, PublicKey(created.data.from.address), (amt * 1_000_000L).roundToLong())
                LOGGER.info("Coin out alert: {}, {} ({})", created.data.from.address, amt, t)
                rest.channel.editMessage(message.channelId, message.id) {
                    this.content = "$t"
                }
            }

            "sol" -> {
                val amt = args[0].toDoubleOrNull() ?: return@on
                try {
                    wallet.balanceSOL(PublicKey(address))
                } catch (e: IllegalArgumentException) {
                    rest.channel.editMessage(message.channelId, message.id) {
                        this.content = "Illegal address."
                    }
                    return@on
                }

                val t = wallet.sendSPL(hot, PublicKey(address), (amt * 1_000_000L).roundToLong())
                LOGGER.info("Coin out alert: {}, {} ({})", address, amt, t)
                rest.channel.editMessage(message.channelId, message.id) {
                    this.content = "$t"
                }
            }
        }
    }

    gateway.on<MessageCreate> {
        if (message.author.id.toString() !in whitelist) return@on
        val sentByMe = message.author.id == rest.user.getCurrentUser().id
        val content = message.content
        val raw = content.drop(prefix.length)
        val cmd = raw.substringBefore(' ')
        val args = raw.split(' ').drop(1)
        if (cmd.startsWith(prefix)) return@on

        when (cmd.lowercase()) {
            "purge", "ㅔㅕㄱㅎㄷ" -> {
                if (!sentByMe) return@on
                rest.channel.deleteMessage(message.channelId, message.id)
                val amount = minOf(15, args[0].toIntOrNull() ?: return@on)
                rest.channel.getMessages(message.channelId)
                    .filter { it.author.id == message.author.id }
                    .let { it.take(minOf(it.size, amount)) }
                    .map { it.id }.mapIndexed { i, it ->
                        GlobalScope.launch {
                            delay((i * 10L * Random.nextDouble(0.1, 0.8)).toLong())
                            rest.channel.deleteMessage(message.channelId, it)
                        }
                    }.forEach { it.join() }
            }

            "cur", "쳑" -> {
                rest.send(
                    message, sentByMe, """
                    |# Current address: '${hot.publicKey}'
                    |```
                    |${wallet.balanceSOL(hot)} SOL
                    |${wallet.balanceUSDT(hot)} USDT
                    |```
                    |``Powered by Solana``""".trimMargin()
                )
            }

            "recv", "ㄱㄷㅊㅍ", "receive" -> {
                val currency = args[0].uppercase()
                if (currency == "USDTSOL") {
                    rest.send(message, sentByMe, "${hot.publicKey}")
                    return@on
                } else {
                    val t = try {
                        ff.price(
                            PriceRequest(
                                SwapType.FLOAT,
                                currency,
                                "USDTSOL",
                                Direction.TO,
                                1.0,
                            )
                        )
                    } catch (e: Exception) {
                        LOGGER.error("Error while identifying coin", e)
                        null
                    }
                    if (t == null) {
                        rest.send(message, sentByMe, "Failed to Identify coin or unable to perform exchange for now.")
                        return@on
                    }
                    val created = ff.create(
                        CreateRequest(
                            SwapType.FLOAT,
                            currency,
                            "USDTSOL",
                            Direction.TO,
                            1.0,
                            hot.publicKey.toString(),

                        )
                    )
                    rest.send(message, sentByMe, "``${created.data.from.address}``\nYou must send over 1$ or deposit won't perform!")
                }
            }
        }
    }
    val s = System.currentTimeMillis()
    Runtime.getRuntime().addShutdownHook(Thread {
        LOGGER.info("bruhed with {}ms", System.currentTimeMillis() - s)
        runBlocking { gateway.stop() }
    })

    gateway.start(cfg.token)
}

suspend fun RestClient.send(message: DiscordMessage, sentByMe: Boolean, cnt: String) {
    if (sentByMe)
        channel.editMessage(message.channelId, message.id) {
            this.content = cnt
        }
    else
        channel.createMessage(message.channelId) {
            this.content = cnt
        }
}