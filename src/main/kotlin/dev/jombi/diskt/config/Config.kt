package dev.jombi.diskt.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(val token: String, val sender: String, val mnemonic: String, val ffApiKey: String, val ffApiSecret: String)
