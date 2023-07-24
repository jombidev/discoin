package dev.jombi.diskt.ff.impl

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val id: String,
    val token: String
)