package dev.jombi.diskt.ff.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class SwapType {
    @SerialName("fixed") FIXED,
    @SerialName("float") FLOAT
}

enum class Direction {
    @SerialName("from") FROM,
    @SerialName("to") TO
}

@Serializable(with = OrderStatus.CustomOrderStatusSerializer::class)
enum class OrderStatus {
    NEW, PENDING, EXCHANGE, WITHDRAW, DONE, EXPIRED, EMERGENCY;
    internal object CustomOrderStatusSerializer : KSerializer<OrderStatus> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("status", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): OrderStatus = valueOf(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: OrderStatus) = encoder.encodeString(value.name)

    }
}