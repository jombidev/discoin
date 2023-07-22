package dev.kord.rest.builder

import dev.kord.common.annotation.KordDsl
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.nio.file.Path


@KordDsl
public interface AuditBuilder {
    /**
     * The reason for this request, this will be displayed in the audit log.
     */
    public var reason: String?
}

@KordDsl
public interface RequestBuilder<out T : Any> {
    public fun toRequest(): T
}

@KordDsl
public interface AuditRequestBuilder<out T : Any> : AuditBuilder, RequestBuilder<T>