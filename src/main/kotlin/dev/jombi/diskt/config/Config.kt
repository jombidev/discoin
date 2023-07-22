package dev.jombi.diskt.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(val token: String, val mnemonic: String)
