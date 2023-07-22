package dev.kord.gateway

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.optional
import dev.kord.gateway.builder.PresenceBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class GatewayConfiguration(
    val token: String,
    val name: String,
    val presence: Optional<DiscordPresence> = Optional.Missing(),
    val threshold: Int,
)

@OptIn(ExperimentalContracts::class)
public class GatewayConfigurationBuilder(
    /**
     * The token of the bot.
     */
    public val token: String,
    /**
     * The name of the library.
     */
    public var name: String = "DisKt",
    /**
     * The presence the bot should show on login.
     */
    public var presence: DiscordPresence? = null,
    /**
     * A value between 50 and 250, representing the maximum amount of members in a guild
     * before the gateway will stop sending info on offline members.
     */
    public var threshold: Int = 250,
) {

    /**
     * Calls the [builder] on a new [PresenceBuilder] and assigns the result to [presence].
     */
    public inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        presence = PresenceBuilder().apply(builder).toPresence()
    }

    /**
     * Returns an immutable version of this builder.
     */
    public fun build(): GatewayConfiguration = GatewayConfiguration(
        token,
        name,
        presence.optional().coerceToMissing(),
        threshold,
    )
}
