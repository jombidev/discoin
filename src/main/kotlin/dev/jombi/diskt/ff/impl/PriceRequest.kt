package dev.jombi.diskt.ff.impl

import dev.jombi.diskt.ff.data.Direction
import dev.jombi.diskt.ff.data.SwapType
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

@Serializable
data class PriceRequest(
    val type: SwapType,
    val fromCcy: String,
    val toCcy: String,
    val direction: Direction,
    val amount: Double,
    val ccies: OptionalBoolean = OptionalBoolean.Missing,
    val usd: OptionalBoolean = OptionalBoolean.Missing,
    val refcode: String? = null,
    val afftax: Float? = null
)