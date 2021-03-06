plugins {
    id("java")
    kotlin("jvm") version "1.5.21"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-dependencies:4.1.1"))
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core")
    implementation("io.vertx:vertx-lang-kotlin") {
        exclude("org.jetbrains.kotlin", "*")
        exclude("org.jetbrains.kotlinx", "*")
    }
    implementation("io.vertx:vertx-lang-kotlin-coroutines")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")

    implementation("com.squareup:kotlinpoet:1.9.0")
    implementation("net.pwall.json:json-kotlin-schema-codegen:0.34")

    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.6.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.5"
        languageVersion = "1.5"
        freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
    }
}

pluginBundle {
    website = "https://github.com/wem/vertx-hermes-gradle-plugin"
    vcsUrl = "https://github.com/wem/vertx-hermes-gradle-plugin.git"
    tags = listOf("dto", "generator", "Vert.x", "Kotlin", "communication", "messagecodec")
}

gradlePlugin {
    plugins {
        create("vertxHermesPlugin") {
            id = "ch.sourcemotion.gradle.vertx.hermes"
            implementationClass = "ch.sourcemotion.vertx.gradle.hermes.plugin.HermesPlugin"
            displayName = "Plugin to generate Kotlin source code around Vert.x and dtos"
            description = "Generates Kotlin source code for dtos, Vert.x message codecs and communication (consumer, sender) based on JSON (schemas). Take a look in the README"
        }
    }
}