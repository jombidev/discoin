package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Represent a [interactable component within a message sent in Discord](https://discord.com/developers/docs/interactions/message-components#what-are-components).
 *
 * @property type the [ComponentType] of the component
 * @property emoji an [DiscordPartialEmoji] that appears on the button (if the component is a button)
 * @property customId a developer-defined identifier for the button, max 100 characters
 * @property url a url for link-style buttons
 * @property disabled whether the button is disabled, default `false`
 * @property components a list of child components (for action rows)
 * @property options the select menu options
 * @property placeholder the placeholder text for the select menu
 * @property minValues the minimum amount of [options] allowed
 * @property maxValues the maximum amount of [options] allowed
 * @property minLength the minimum input length for a text input, min 0, max 4000.
 * @property maxLength the maximum input length for a text input, min 1, max 4000.
 * @property required whether this component is required to be filled, default false.
 * @property value a pre-filled value for this component, max 4000 characters.
 * @property channelTypes List of channel types to include in the channel select component ([ComponentType.ChannelSelect])
 */
@Serializable(with = DiscordComponent.Serializer::class)
public sealed class DiscordComponent {
    public abstract val type: ComponentType
    public abstract val label: Optional<String>
    public abstract val emoji: Optional<DiscordPartialEmoji>
    @SerialName("custom_id")
    public abstract val customId: Optional<String>
    public abstract val url: Optional<String>
    public abstract val disabled: OptionalBoolean
    public abstract val components: Optional<List<DiscordComponent>>
    public abstract val options: Optional<List<DiscordSelectOption>>
    public abstract val placeholder: Optional<String>
    @SerialName("min_values")
    public abstract val minValues: OptionalInt
    @SerialName("max_values")
    public abstract val maxValues: OptionalInt
    @SerialName("min_length")
    public abstract val minLength: OptionalInt
    @SerialName("max_length")
    public abstract val maxLength: OptionalInt
    public abstract val required: OptionalBoolean
    public abstract val value: Optional<String>
    @SerialName("channel_types")
    public abstract val channelTypes: Optional<List<ChannelType>>

    internal object Serializer : JsonContentPolymorphicSerializer<DiscordComponent>(DiscordComponent::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out DiscordComponent> {
            val componentType = element.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: error("Missing component type ID!")

            return if (componentType == ComponentType.TextInput.value) {
                DiscordTextInputComponent.serializer()
            } else {
                DiscordChatComponent.serializer()
            }
        }
    }
}

@Serializable
public data class DiscordChatComponent(
     override val type: ComponentType,
     val style: Optional<ButtonStyle> = Optional.Missing(),
     override val label: Optional<String> = Optional.Missing(),
     override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
     override val customId: Optional<String> = Optional.Missing(),
     override val url: Optional<String> = Optional.Missing(),
     override val disabled: OptionalBoolean = OptionalBoolean.Missing,
     override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
     override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
     override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
     override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
     override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
     override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
     override val maxLength: OptionalInt = OptionalInt.Missing,
     override val required: OptionalBoolean = OptionalBoolean.Missing,
     override val value: Optional<String> = Optional.Missing(),
     @SerialName("channel_types")
     override val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
) : DiscordComponent()

@Serializable
public data class DiscordTextInputComponent(
     override val type: ComponentType,
     public val style: Optional<TextInputStyle> = Optional.Missing(),
     override val label: Optional<String> = Optional.Missing(),
     override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
     override val customId: Optional<String> = Optional.Missing(),
     override val url: Optional<String> = Optional.Missing(),
     override val disabled: OptionalBoolean = OptionalBoolean.Missing,
     override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
     override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
     override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
     override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
     override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
     override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
     override val maxLength: OptionalInt = OptionalInt.Missing,
     override val required: OptionalBoolean = OptionalBoolean.Missing,
     override val value: Optional<String> = Optional.Missing(),
     @SerialName("channel_types")
     override val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
) : DiscordComponent()
