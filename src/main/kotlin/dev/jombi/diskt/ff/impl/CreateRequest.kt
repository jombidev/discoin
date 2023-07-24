package dev.jombi.diskt.ff.impl

import dev.jombi.diskt.ff.data.Direction
import dev.jombi.diskt.ff.data.SwapType
import kotlinx.serialization.Serializable

@Serializable
data class CreateRequest(
    val type: SwapType,
    val fromCcy: String,
    val toCcy: String,
    val direction: Direction,
    val amount: Double,
    val toAddress: String,
    val tag: String? = null,
    val refcode: String? = null,
    val afftax: String? = null
)