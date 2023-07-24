package dev.jombi.diskt.ff.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseBase<T>(
    val code: Int,
    val msg: String,
    val data: T
)