package dev.kord.rest

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.*

public class Image private constructor(public val data: ByteArray, public val format: dev.kord.rest.Image.Format) {

    public val dataUri: String get() = "data:image/${format.extensions.first()};base64,${data.encodeBase64()}"

    public companion object {
        public fun raw(data: ByteArray, format: dev.kord.rest.Image.Format): dev.kord.rest.Image {
            return dev.kord.rest.Image(data, format)
        }

        public suspend fun fromUrl(client: HttpClient, url: String): dev.kord.rest.Image {
            val call = client.get(url)
            val contentType = call.headers["Content-Type"]
                ?: error("expected 'Content-Type' header in image request")

            val bytes = call.body<ByteArray>()

            return dev.kord.rest.Image(bytes, dev.kord.rest.Image.Format.Companion.fromContentType(contentType))
        }
    }

    public sealed class Format(public val extensions: List<String>) {
        protected constructor(vararg extensions: String) : this(extensions.toList())

        public val extension: String get() = extensions.first()

        public object JPEG : dev.kord.rest.Image.Format("jpeg", "jpg")
        public object PNG : dev.kord.rest.Image.Format("png")
        public object WEBP : dev.kord.rest.Image.Format("webp")
        public object GIF : dev.kord.rest.Image.Format("gif")
        public object LOTTIE : dev.kord.rest.Image.Format("json")

        public companion object {
            public val values: Set<dev.kord.rest.Image.Format>
                get() = setOf(
                    dev.kord.rest.Image.Format.JPEG,
                    dev.kord.rest.Image.Format.PNG,
                    dev.kord.rest.Image.Format.WEBP,
                    dev.kord.rest.Image.Format.GIF,
                    dev.kord.rest.Image.Format.LOTTIE,
                )

            public fun isSupported(fileName: String): Boolean {
                return dev.kord.rest.Image.Format.Companion.values.any {
                    it.extensions.any { extension -> fileName.endsWith(extension, true) }
                }
            }

            public fun fromContentType(type: String): dev.kord.rest.Image.Format = when (type) {
                "image/jpeg" -> dev.kord.rest.Image.Format.JPEG
                "image/png" -> dev.kord.rest.Image.Format.PNG
                "image/webp" -> dev.kord.rest.Image.Format.WEBP
                "image/gif" -> dev.kord.rest.Image.Format.GIF
                "application/json" -> dev.kord.rest.Image.Format.LOTTIE
                else -> error(type)
            }
        }
    }

    /**
     * Represents size of the [Image], for requesting different sizes of Image from the Discord.
     * Both height and width of the [Image] will always be smaller than or equal to [maxRes] of the [Size].
     */
    public enum class Size(public val maxRes: Int) {
        Size16(16),
        Size32(32),
        Size64(64),
        Size128(128),
        Size256(256),
        Size512(512),
        Size1024(1024),
        Size2048(2048),
        Size4096(4096),
    }
}
