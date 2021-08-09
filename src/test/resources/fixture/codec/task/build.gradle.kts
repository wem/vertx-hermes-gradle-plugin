import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo

plugins {
    java
    id("ch.sourcemotion.gradle.vertx.hermes")
}

hermes {
    codec {
        packageName.set("ch.sourcemotion.hermes.test")
        classesInfo.set(listOf(DtoClassInfo("ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec", "TestDto")))
    }
}