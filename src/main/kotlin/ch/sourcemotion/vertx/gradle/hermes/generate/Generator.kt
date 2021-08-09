package ch.sourcemotion.vertx.gradle.hermes.generate

abstract class Generator<T: GeneratorConfiguration, R> {
    abstract fun generate(configuration: T) : R
}