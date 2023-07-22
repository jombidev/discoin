@file:Suppress("unused")

package com.solana.api

import com.solana.core.PublicKey
import com.solana.exception.SolanaException

import com.solana.networking.RpcRequest
import com.solana.networking.SolanaResponseSerializer
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

class GetTokenSupplyRequest(tokenMint: PublicKey) : RpcRequest() {
    override val method: String = "getTokenSupply"
    override val params = buildJsonArray {
        add(tokenMint.toString())
    }
}

internal fun getTokenSupplySerializer() = SolanaResponseSerializer(TokenAmountInfoResponse.serializer())

suspend fun Api.getTokenSupply(tokenMint: PublicKey): Result<TokenAmountInfoResponse> =
    router.makeRequestResult(GetTokenSupplyRequest(tokenMint), getTokenSupplySerializer())
        .let { result ->
            @Suppress("UNCHECKED_CAST")
            if (result.isSuccess && result.getOrNull() == null)
                Result.failure(SolanaException("Can not be null"))
            else result as Result<TokenAmountInfoResponse> // safe cast, null case handled above
        }

fun Api.getTokenSupply(tokenMint: PublicKey, onComplete: (Result<TokenAmountInfoResponse>) -> Unit) {
    CoroutineScope(dispatcher).launch {
        onComplete(getTokenSupply(tokenMint))
    }
}