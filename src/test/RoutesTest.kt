package dev.danielwright

import dev.danielwright.main.module
import dev.danielwright.main.pokemonAppModule
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinListener
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.test.KoinTest

class RoutesTest : FunSpec(), KoinTest {

    init {
        test("hello world route returns 200 status code") {
            withTestApplication(Application::module) {
                handleRequest(HttpMethod.Get, "/hello").apply {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.content shouldContain "Hello, World!"
                }
            }
        }
    }
}
