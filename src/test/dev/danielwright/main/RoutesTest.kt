package dev.danielwright.main

import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock

class RoutesTest : FunSpec(), KoinTest {

    @get:Rule
    val mockProvider = MockProvider.register { clazz ->
        mockkClass(clazz)
    }

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        stopKoin()
    }

    init {
        test("hello world route returns 200 status code") {
            withTestApplication(Application::module) {
                handleRequest(HttpMethod.Get, "/hello").apply {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.content shouldContain "Hello, World!"
                }
            }
        }

        test("pokemonAPI returns pokemon") {

            withTestApplication(Application::module) {

                val pokemonApi = declareMock<PokemonAPIClient>()
                coEvery { pokemonApi.getPokemon(range(1, 150)) } returns Pokemon(1, "test", 0, 0)

                handleRequest(HttpMethod.Get, "/pokemon").apply {
                    response shouldHaveStatus HttpStatusCode.OK
                    val content: String = response.content ?: throw Exception()
                    val actual = Json.decodeFromString<Pokemon>(content)

                    actual.id shouldBe 1

                    coVerify { pokemonApi.getPokemon(range(1, 150)) }
                }
            }
        }

        test("mock engine") {

            withTestApplication(Application::module) {

                val httpClient = declareMock<PokemonHttpClient>()

                val mockEngine = HttpClient(MockEngine) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            }
                        )
                    }
                    engine {
                        addHandler { request ->
                            when (request.url.host) {
                                "pokeapi.co" -> {
                                    val responseHeaders =
                                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                                    respond(
                                        """{ "id": 1, "name": "test", "height": 0, "weight": 0}""",
                                        headers = responseHeaders
                                    )
                                }
                                else -> error("Unhandled ${request.url}")
                            }
                        }
                    }
                }

                every { httpClient.getClient() } returns mockEngine

                handleRequest(HttpMethod.Get, "/pokemon").apply {
                    response shouldHaveStatus HttpStatusCode.OK
                    val content: String = response.content ?: throw Exception()
                    val actual = Json.decodeFromString<Pokemon>(content)
                    actual.id shouldBe 1
                }
            }
        }
    }
}
