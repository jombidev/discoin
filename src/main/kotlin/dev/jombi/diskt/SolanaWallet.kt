package dev.jombi.diskt

import com.solana.Solana
import com.solana.api.*
import com.solana.core.HotAccount
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.core.TransactionInstruction
import com.solana.models.buffer.AccountInfoData
import com.solana.networking.serialization.serializers.base64.BorshAsBase64JsonArraySerializer
import com.solana.programs.AssociatedTokenProgram
import com.solana.programs.TokenProgram
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger(SolanaWallet::class.java)

class SolanaWallet(private val sol: Solana) {
    suspend fun balanceSOL(acc: HotAccount): Double = balanceSOL(acc.publicKey)
    suspend fun balanceSOL(acc: PublicKey): Double {
        return sol.api.getBalance(acc).getOrThrow().toDouble() / 1_000_000_000
    }


    private val TOKEN_ADDRESS = PublicKey("Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB")

    fun getAssociatedTokenAddress(acc: PublicKey) = PublicKey.associatedTokenAddress(acc, TOKEN_ADDRESS)

    suspend fun getUserInfo(acc: HotAccount): AccountInfo<AccountInfoData?>? = getUserInfo(acc.publicKey)

    suspend fun getUserInfo(acc: PublicKey): AccountInfo<AccountInfoData?>? = getUserInfo(getAssociatedTokenAddress(acc))

    suspend fun getUserInfo(acc: PublicKey.ProgramDerivedAddress): AccountInfo<AccountInfoData?>? {
        val serializer = accountInfoSerializer(BorshAsBase64JsonArraySerializer((AccountInfoData.serializer())))
        return sol.api.getAccountInfo(serializer, acc.address).getOrNull()
    }

    suspend fun balanceUSDT(acc: HotAccount): Double? {
        val t = getUserInfo(acc)
        return t?.data?.lamports?.let { it.toDouble() / 1_000_000 }
    }

    suspend fun balanceUSDT(acc: PublicKey): Double? {
        val t = getUserInfo(acc)
        return t?.data?.lamports?.let { it.toDouble() / 1_000_000 }
    }

    suspend fun tryCreateAssociatedAccount(feePayer: HotAccount, pk: PublicKey): String? {
        val associatedTokenTo = getAssociatedTokenAddress(pk)
        val transaction = Transaction()
        val transactionInstructions = arrayListOf<TransactionInstruction>()
        val accountTo = getUserInfo(associatedTokenTo)
        if (accountTo != null) return null

        transactionInstructions.add(
            AssociatedTokenProgram.createAssociatedTokenAccountInstruction(
                mint = TOKEN_ADDRESS,
                associatedAccount = associatedTokenTo.address,
                owner = pk,
                payer = feePayer.publicKey,
            )
        )

        transaction.add(*transactionInstructions.toTypedArray())

        return sol.api.sendTransaction(transaction, listOf(feePayer)).getOrThrow()
    }

    suspend fun sendSPL(from: HotAccount, to: PublicKey, amount: Long, feePayer: HotAccount? = null): String? {
        val acc = feePayer ?: from
        val associatedTokenFrom = getAssociatedTokenAddress(from.publicKey)
        val accountFrom = getUserInfo(associatedTokenFrom)
        if (accountFrom == null) {
            LOGGER.info("got null UserInfo. ignoring...")
            return null
        }

        val associatedTokenTo = getAssociatedTokenAddress(to)
        val transaction = Transaction()
        val transactionInstructions = arrayListOf<TransactionInstruction>()
        val accountTo = getUserInfo(associatedTokenTo)

        if (accountTo == null) {
            transactionInstructions.add(
                AssociatedTokenProgram.createAssociatedTokenAccountInstruction(
                    mint = TOKEN_ADDRESS,
                    associatedAccount = associatedTokenTo.address,
                    owner = to,
                    payer = acc.publicKey,
                )
            )
        }

        transactionInstructions.add(
            TokenProgram.transfer(
                associatedTokenFrom.address,
                associatedTokenTo.address,
                amount,
                from.publicKey,
            )
        )

        transaction.add(*transactionInstructions.toTypedArray())
        val t = sol.api.sendTransaction(transaction, if (feePayer == null) listOf(acc) else listOf(acc, from))
        return if (t.isSuccess) t.getOrThrow() else t.exceptionOrNull()?.let { it.message ?: it.stackTraceToString() }
    }
}