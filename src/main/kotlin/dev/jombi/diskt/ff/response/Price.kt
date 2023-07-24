package dev.jombi.diskt.ff.response

import kotlinx.serialization.Serializable

@Serializable
data class Price(
    val from: ExchangeInfo,
    val to: ExchangeInfo,
    val errors: List<String>,
    val ccies: List<CurrenciesMin>? = null
)

@Serializable
data class ExchangeInfo(
    override val code: String,
    override val network: String,
    override val coin: String,
    val amount: String,
    val rate: String,
    val precision: Int,
    val min: String,
    val max: String,
    val usd: String,
    val btc: String? = null
) : CurrencyBase