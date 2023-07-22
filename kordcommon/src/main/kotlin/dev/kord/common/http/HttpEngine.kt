package dev.kord.common.http

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

/** @suppress */
public object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
