package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.generate.AbstractGeneratorTest
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.function.Function

internal class MessageCodecGeneratorTest : AbstractGeneratorTest() {

    private companion object {
        const val EXPECTED_SYSTEM_CODEC_ID: Byte = -1
        val alphaMessageCodecClassName = "${AlphaDto::class.java.packageName}.AlphaDtoMessageCodec"
        val betaMessageCodecClassName = "${BetaDto::class.java.packageName}.BetaDtoMessageCodec"
        const val CODEC_FILE_NAME = "DtoMessageCodec"
        val messageCodecNameSupplier: Function<DtoClassInfo, String> = Function {
            "${it.className}MessageCodec"
        }
    }

    @Test
    internal fun generate_single_dto(@TempDir tempDir: File) {
        val configuration = MessageCodecGeneratorConfiguration(
            tempDir, AlphaDto::class.java.packageName, CODEC_FILE_NAME,
            listOf(AlphaDto::class.java.toGeneratedClass()), messageCodecNameSupplier
        )

        val sut = MessageCodecGenerator()
        val outputFilePath = sut.generate(configuration)
        val messageCodecClass = compileSourceFile(outputFilePath, alphaMessageCodecClassName)
        val messageCodec =
            messageCodecClass.getDeclaredConstructor().newInstance() as MessageCodec<AlphaDto?, AlphaDto?>

        messageCodec.verify(AlphaDto("some-value"), "AlphaDtoMessageCodec")
    }

    @Test
    internal fun generate_multiple_dtos(@TempDir tempDir: File) {
        val configuration = MessageCodecGeneratorConfiguration(
            tempDir, AlphaDto::class.java.packageName, CODEC_FILE_NAME,
            listOf(AlphaDto::class.java.toGeneratedClass(),BetaDto::class.java.toGeneratedClass()),
            messageCodecNameSupplier
        )

        val sut = MessageCodecGenerator()
        val outputFilePath = sut.generate(configuration)

        val messageCodecClasses =
            compileSourceFile(outputFilePath, alphaMessageCodecClassName, betaMessageCodecClassName)

        (messageCodecClasses[alphaMessageCodecClassName].shouldNotBeNull().getDeclaredConstructor()
            .newInstance() as MessageCodec<AlphaDto?, AlphaDto?>)
            .verify(AlphaDto("some-value"), "AlphaDtoMessageCodec")
        (messageCodecClasses[betaMessageCodecClassName].shouldNotBeNull().getDeclaredConstructor()
            .newInstance() as MessageCodec<BetaDto?, BetaDto?>)
            .verify(BetaDto("some-value"), "BetaDtoMessageCodec")
    }

    private fun <T> MessageCodec<T, T>.verify(dto: T, expectedCodecAndClassName: String) {
        this::class.java.simpleName.shouldBe(expectedCodecAndClassName)
        name().shouldBe(expectedCodecAndClassName)
        systemCodecID().shouldBe(EXPECTED_SYSTEM_CODEC_ID)

        transform(null).shouldBeNull()
        transform(dto).shouldBe(dto)
        var wireBuffer = Buffer.buffer()
        encodeToWire(wireBuffer, dto)
        decodeFromWire(0, wireBuffer).shouldBe(dto)

        wireBuffer = Buffer.buffer()
        encodeToWire(wireBuffer, null)
        decodeFromWire(0, wireBuffer).shouldBeNull()
    }

    private fun Class<*>.toGeneratedClass() = DtoClassInfo(packageName, simpleName)
}

data class AlphaDto(val field: String)
data class BetaDto(val field: String)