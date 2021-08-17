plugins {
    java
    id("ch.sourcemotion.gradle.vertx.hermes")
}

hermes {
    communication {
        packageName.set("ch.sourcemotion.hermes.test")
    }
}