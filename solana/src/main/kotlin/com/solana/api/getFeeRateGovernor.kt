@file:Suppress("unused")

package com.solana.api

import com.solana.exception.SolanaException
import com.solana.networking.RpcRequest
import com.solana.networking.SolanaResponseSerializer
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class GetFeeRateGovernorRequest : RpcRequest() {
    override val method: String = "getFeeRateGovernor"
}

@Serializable
data class FeeRateGovernor (
    val burnPercent: Long,
    val maxLamportsPerSignature: Long,
    val minLamportsPerSignature: Long,
    val targetLamportsPerSignature: Long,
    val targetSignaturesPerSlot: Long
)

@Serializable
data class FeeRateGovernorInfo (
    val feeRateGovernor: FeeRateGovernor
)

internal fun getFeeRateGovernorSerializer() = SolanaResponseSerializer(FeeRateGovernorInfo.serializer())

fun Api.getFeeRateGovernor(onComplete: ((Result<FeeRateGovernorInfo>) -> Unit)) {
    CoroutineScope(dispatcher).launch {
        onComplete(getFeeRateGovernor())
    }
}

suspend fun Api.getFeeRateGovernor(): Result<FeeRateGovernorInfo> =
    router.makeRequestResult(GetFeeRateGovernorRequest(), getFeeRateGovernorSerializer())
        .let { result ->
            @Suppress("UNCHECKED_CAST")
            if (result.isSuccess && result.getOrNull() == null)
                Result.failure(SolanaException("Can not be null"))
            else result as Result<FeeRateGovernorInfo> // safe cast, null case handled above
        }