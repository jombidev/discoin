@file:Suppress("unused")

package com.solana.api

import com.solana.exception.SolanaException
import com.solana.networking.RpcRequest
import com.solana.networking.SolanaResponseSerializer
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class GetFeesRequest : RpcRequest() {
    override val method: String = "getFees"
}

@Serializable
class FeesInfo (
    val blockhash: String,
    val feeCalculator: FeeCalculator,
    val lastValidSlot: Long,
    val lastValidBlockHeight: Long
)

internal fun getFeesSerializer() = SolanaResponseSerializer(FeesInfo.serializer())

fun Api.getFees(onComplete: ((Result<FeesInfo>) -> Unit)){
    CoroutineScope(dispatcher).launch {
        onComplete(getFees())
    }
}

suspend fun Api.getFees(): Result<FeesInfo> =
    router.makeRequestResult(GetFeesRequest(), getFeesSerializer())
        .let { result ->
            @Suppress("UNCHECKED_CAST")
            if (result.isSuccess && result.getOrNull() == null)
                Result.failure(SolanaException("Can not be null"))
            else result as Result<FeesInfo> // safe cast, null case handled above
        }