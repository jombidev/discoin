package dev.kord.common.entity

import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordApplicationRoleConnectionMetadata(
    val type: ApplicationRoleConnectionMetadataType,
    val key: String,
    val name: String,
    @SerialName("name_localizations")
    val nameLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
    val description: String,
    @SerialName("description_localizations")
    val descriptionLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
)
