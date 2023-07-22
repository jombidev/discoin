package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordAutoModerationRule(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val name: String,
    @SerialName("creator_id")
    val creatorId: Snowflake,
    @SerialName("event_type")
    val eventType: AutoModerationRuleEventType,
    @SerialName("trigger_type")
    val triggerType: AutoModerationRuleTriggerType,
    @SerialName("trigger_metadata")
    val triggerMetadata: DiscordAutoModerationRuleTriggerMetadata,
    val actions: List<DiscordAutoModerationAction>,
    val enabled: Boolean,
    @SerialName("exempt_roles")
    val exemptRoles: List<Snowflake>,
    @SerialName("exempt_channels")
    val exemptChannels: List<Snowflake>,
)

@Serializable
public data class DiscordAutoModerationRuleTriggerMetadata(
    @SerialName("keyword_filter")
    val keywordFilter: Optional<List<String>> = Optional.Missing(),
    @SerialName("regex_patterns")
    val regexPatterns: Optional<List<String>> = Optional.Missing(),
    val presets: Optional<List<AutoModerationRuleKeywordPresetType>> = Optional.Missing(),
    @SerialName("allow_list")
    val allowList: Optional<List<String>> = Optional.Missing(),
    @SerialName("mention_total_limit")
    val mentionTotalLimit: OptionalInt = OptionalInt.Missing,
    @SerialName("mention_raid_protection_enabled")
    val mentionRaidProtectionEnabled: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
public data class DiscordAutoModerationAction(
    val type: AutoModerationActionType,
    val metadata: Optional<DiscordAutoModerationActionMetadata> = Optional.Missing(),
)

@Serializable
public data class DiscordAutoModerationActionMetadata(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("duration_seconds")
    val durationSeconds: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("custom_message")
    val customMessage: Optional<String> = Optional.Missing(),
)
