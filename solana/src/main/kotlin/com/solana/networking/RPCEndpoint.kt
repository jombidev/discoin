package com.solana.networking

import java.net.URL

const val MAINNETBETA = "mainnet-beta"
const val DEVNET = "devnet"
const val TESTNET = "testnet"

sealed class Network(val name: String) {
    object mainnetBeta: Network(MAINNETBETA)
    object devnet: Network(DEVNET)
    object testnet: Network(TESTNET)
    var cluster: String = this.name
}


sealed class RPCEndpoint(open val url: URL, open val urlWebSocket: URL, open val network: Network) {
    data object MainNetworkBeta: RPCEndpoint(URL("https://api.mainnet-beta.solana.com"), URL("https://api.mainnet-beta.solana.com"), Network.mainnetBeta)
    data object DevNetwork: RPCEndpoint(URL("https://api.devnet.solana.com"), URL("https://api.devnet.solana.com"), Network.devnet)
    data object TestNetwork: RPCEndpoint(URL("https://testnet.solana.com"), URL("https://testnet.solana.com"),Network.testnet)
    data class Custom(override val url: URL, override val urlWebSocket: URL, override val network: Network) : RPCEndpoint(url, urlWebSocket, network)
}