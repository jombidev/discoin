package dev.jombi.diskt.ff.response

import dev.jombi.diskt.ff.data.OrderStatus
import dev.jombi.diskt.ff.data.SwapType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Order(
    val token: String,
    val id: String,
    val type: SwapType,
    val status: OrderStatus,
    val time: CreateTime,
    val from: CreateInfo,
    val to: CreateInfo,
    val back: JsonObject,
    val email: String,
    val emergency: JsonObject // lazy moment
)

@Serializable
data class CreateTime(
    val reg: Long?,
    val start: Long?,
    val finish: Long?,
    val update: Long?,
    val expiration: Long?,
    val left: Long?,
)

@Serializable
data class CreateInfo(
    override val code: String,
    override val coin: String,
    override val network: String,
    override val name: String,
    val alias: String,
    val amount: String,
    val address: String,
    val tag: String?,
    val tagName: String?,
    val tx: Tx,
) : CurrencyBase, CurrencyName

@Serializable
data class Tx(
    val id: String?,
    val amount: String?,
    val fee: String?,
    val ccyfee: String?,
    val timeReg: String?,
    val timeBlock: String?,
    val confirmations: String?,
)