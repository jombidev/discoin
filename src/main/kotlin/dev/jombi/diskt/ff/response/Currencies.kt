package dev.jombi.diskt.ff.response

import kotlinx.serialization.Serializable

@Serializable
data class Currencies(
    override val code: String,
    override val coin: String,
    override val network: String,
    val priority: Int,
    override val name: String,
    val recv: Int,
    val send: Int,
    val tag: String?,
    val logo: String,
    val color: String
) : CurrencyBase, CurrencyName



interface CurrencyBase : CurrencyCode {
    val coin: String
    val network: String
}

interface CurrencyCode {
    val code: String
}

interface CurrencyName {
    val name: String
}

@Serializable
data class CurrenciesMin(
    override val code: String,
    val recv: Boolean,
    val send: Boolean
) : CurrencyCode