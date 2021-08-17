package ch.sourcemotion.vertx.gradle.hermes

import java.io.File

interface DefaultTaskTest {
    fun with_default_values()
    fun disabled()
    fun execute_task(tempDir: File)
}

interface DefinesInputDirImplTest {
    fun with_alternative_input_dir_on_task()
    fun with_alternative_input_dir_on_extension()
}

interface DefinesPackageNameImplTest {
    fun with_package_name_on_task()
    fun with_package_name_on_extension()
}

interface DefinesOutputDirImplTest {
    fun with_alternative_output_dir_on_task()
    fun with_alternative_output_dir_on_extension()
}

interface DefinesClassesInfoImplTest {
    fun with_alternative_classes_info_on_task()
    fun with_alternative_classes_info_on_extension()
}

interface DefinesGenerateSenderImplTest {
    fun with_alternative_generate_sender_on_task()
    fun with_alternative_generate_sender_on_extension()
}

interface DefinesGenerateConsumerImplTest {
    fun with_alternative_generate_consumer_on_task()
    fun with_alternative_generate_consumer_on_extension()
}