package dev.jombi.diskt.ff

import dev.jombi.diskt.ff.impl.CreateRequest
import dev.jombi.diskt.ff.impl.OrderRequest
import dev.jombi.diskt.ff.impl.PriceRequest
import dev.jombi.diskt.ff.response.Order
import dev.jombi.diskt.ff.response.Currencies
import dev.jombi.diskt.ff.response.Price
import dev.jombi.diskt.ff.response.ResponseBase
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val LOGGER = LoggerFactory.getLogger(FFApi::class.java)

class FFApi(private val apiKey: String, private val apiSecret: String) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun currencies(): ResponseBase<List<Currencies>> {
        return json.decodeFromString(
            ResponseBase.serializer(ListSerializer(Currencies.serializer())),
            sendPost("ccies", null)
        )
    }

    suspend fun price(request: PriceRequest): ResponseBase<Price> {
        return json.decodeFromString(
            ResponseBase.serializer(Price.serializer()),
            sendPost("price", request, PriceRequest.serializer())
        )
    }

    suspend fun create(request: CreateRequest): ResponseBase<Order> {
        val res = sendPost("create", request, CreateRequest.serializer())
        LOGGER.info(res)
        return json.decodeFromString(
            ResponseBase.serializer(Order.serializer()),
            res
        )
    }

    suspend fun order(request: OrderRequest): ResponseBase<Order> {
        return json.decodeFromString(
            ResponseBase.serializer(Order.serializer()),
            sendPost("order", request, OrderRequest.serializer())
        )
    }

    private fun sign(msg: String): String {
        val alg = "HmacSHA256"
        val signingKey = SecretKeySpec(apiSecret.toByteArray(), alg)
        val mac = Mac.getInstance(alg)
        mac.init(signingKey)

        val bytes = mac.doFinal(msg.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun <T> sendPost(endpoint: String, req: T?, serializer: KSerializer<T>? = null): String {
        val con = client.post("https://ff.io/api/v2/$endpoint") {
            val t = req?.let { Json.encodeToString(serializer!!, it) }
            header("X-API-KEY", apiKey)
            header("X-API-SIGN", sign(t ?: ""))
            contentType(ContentType.Application.Json.withParameter("charset", "UTF-8"))
            accept(ContentType.Any)

            if (t != null)
                setBody(t)
        }
        return con.bodyAsText()
    }
}