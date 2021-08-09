package ch.sourcemotion.vertx.gradle.hermes.generate

import java.io.Serializable

/**
 * Generated classes are not loadable during the most of the Plugin execution time.
 * This class represents all needed information for subsequent processing.
 */
data class DtoClassInfo(val packageName: String, val className: String) : Serializable