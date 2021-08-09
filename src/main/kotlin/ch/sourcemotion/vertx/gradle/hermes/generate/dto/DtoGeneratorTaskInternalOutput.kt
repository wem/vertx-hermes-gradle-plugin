package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Internal
import javax.inject.Inject

abstract class DtoGeneratorTaskInternalOutput @Inject constructor(objectFactory: ObjectFactory) : BuildService<BuildServiceParameters.None> {
    val generatedClasses: ListProperty<DtoClassInfo> = objectFactory.listProperty(DtoClassInfo::class.java)
}