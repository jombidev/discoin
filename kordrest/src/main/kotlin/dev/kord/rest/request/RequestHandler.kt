package dev.kord.rest.request

import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent

/**
 * Handles Discord API requests.
 */
public interface RequestHandler {

    /**
     * The Discord bot authorization token used on requests.
     */
    public val token: String

    /**
     * Executes the [request], abiding by the active rate limits and returning the response [R].
     *
     * @throws RestRequestException when a non-rate limit error response is returned.
     */
    public suspend fun <B : Any, R> handle(request: Request<B, R>): R

    public suspend fun <T> intercept(builder: RequestBuilder<T>) {
        builder.apply {
            unencodedHeader(UserAgent, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/115.0")
            if (route.requiresAuthorizationHeader) {
                unencodedHeader(Authorization, token) // SELFBOT
            }
        }
    }
}
