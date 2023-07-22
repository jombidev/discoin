import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.kotlin

fun DependencyHandlerScope.sharedDependency(): List<Any> {
    val ktorVersion = "2.3.2"
    return listOf(
        kotlin("stdlib"),
        "ch.qos.logback:logback-classic:1.4.8",

        // *** kotlinx ***
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1",
        "org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1",
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2",
        "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0",
        "org.jetbrains.kotlinx:atomicfu:0.21.0",

        // *** KTOR ***
        "io.ktor:ktor-client-core:$ktorVersion",
        "io.ktor:ktor-client-cio:$ktorVersion",
        "io.ktor:ktor-client-websockets:$ktorVersion",
        "io.ktor:ktor-client-logging:$ktorVersion",
        "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion",
        "io.ktor:ktor-client-content-negotiation:$ktorVersion"
    )
}