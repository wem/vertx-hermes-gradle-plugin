plugins {
    java
    id("ch.sourcemotion.gradle.vertx.hermes")
}

hermes {
    dto {
        packageName.set("ch.sourcemotion.hermes.test")
    }
}