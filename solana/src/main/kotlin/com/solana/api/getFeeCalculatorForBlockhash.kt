@file:Suppress("unused")

package com.solana.api

import com.solana.exception.SolanaException
import com.solana.networking.RpcRequest
import com.solana.networking.SolanaResponseSerializer
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

class GetFeeCalculatorForBlockhashRequest(blockhash: String) : RpcRequest() {
    override val method: String = "getFeeCalculatorForBlockhash"
    override val params = buildJsonArray {
        add(blockhash)
    }
}

@Serializable
data class FeeCalculatorInfo(
    val feeCalculator: FeeCalculator
)

@Serializable
data class FeeCalculator (
    val lamportsPerSignature: Long
)

internal fun getFeeCalculatorForBlockhashSerializer() = SolanaResponseSerializer(FeeCalculatorInfo.serializer())

fun Api.getFeeCalculatorForBlockhash(blockhash: String, onComplete: ((Result<FeeCalculatorInfo>) -> Unit)) {
    CoroutineScope(dispatcher).launch {
        onComplete(getFeeCalculatorForBlockhash(blockhash))
    }
}

suspend fun Api.getFeeCalculatorForBlockhash(blockhash: String): Result<FeeCalculatorInfo> =
    router.makeRequestResult(GetFeeCalculatorForBlockhashRequest(blockhash), getFeeCalculatorForBlockhashSerializer())
        .let { result ->
            @Suppress("UNCHECKED_CAST")
            if (result.isSuccess && result.getOrNull() == null)
                Result.failure(SolanaException("Can not be null"))
            else result as Result<FeeCalculatorInfo> // safe cast, null case handled above
        }