@file:Suppress("unused")

package com.solana.api

import com.solana.exception.SolanaException
import com.solana.networking.RpcRequest
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class GetGenesisHashBlockRequest : RpcRequest() {
    override val method: String = "getGenesisHash"
}

internal fun getGenesisHashBlockSerializer() = String.serializer()

fun Api.getGenesisHash(onComplete: ((Result<String>) -> Unit)){
    CoroutineScope(dispatcher).launch {
        onComplete(getGenesisHash())
    }
}

suspend fun Api.getGenesisHash(): Result<String> =
    router.makeRequestResult(GetGenesisHashBlockRequest(), getGenesisHashBlockSerializer())
        .let { result ->
            @Suppress("UNCHECKED_CAST")
            if (result.isSuccess && result.getOrNull() == null)
                Result.failure(SolanaException("Can not be null"))
            else result as Result<String> // safe cast, null case handled above
        }