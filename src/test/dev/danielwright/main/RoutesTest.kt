package dev.danielwright.dev.danielwright.main

import dev.danielwright.main.Pokemon
import dev.danielwright.main.PokemonAPIClient
import dev.danielwright.main.PokemonHttpClient
import dev.danielwright.main.module
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.server.testing.*
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
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        })
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
