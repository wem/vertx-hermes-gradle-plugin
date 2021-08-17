package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

interface CommunicationGenerator {

    companion object {
        val unitTypeName = Unit::class.java.asClassName()
    }

    fun evaluateTypeName(typeName: String?, fallBack: TypeName): TypeName =
        if (typeName != null) {
            if (typeName.endsWith("?")) {
                ClassName.bestGuess(typeName.substringBefore("?")).copy(nullable = true)
            } else {
                ClassName.bestGuess(typeName)
            }
        } else fallBack
}