import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

val ktorVersion: String by project

group = "com.solana"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    sharedDependency().forEach(::implementation)
    listOf(
        "com.google.protobuf:protobuf-javalite:3.23.2",
        "org.bitcoinj:bitcoinj-core:0.16.2",
        "net.i2p.crypto:eddsa:0.3.0",
    ).forEach(::implementation)
}

kotlin {
    compilerOptions {
        jvmToolchain(11)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}