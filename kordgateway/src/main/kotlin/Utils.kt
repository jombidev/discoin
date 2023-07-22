package dev.kord.gateway

import org.slf4j.Logger

@PublishedApi
internal fun Logger.error(throwable: Throwable): Unit = this.error("", throwable)
