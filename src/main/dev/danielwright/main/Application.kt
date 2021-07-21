package dev.danielwright.main

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.content.TextContent
import io.ktor.features.BadRequestException
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.response.respond
import io.ktor.serialization.json
import kotlinx.serialization.SerializationException
import org.koin.ktor.ext.Koin
import java.util.UUID

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(Koin) {
        modules(pokemonAppModule)
    }
    install(CallLogging) {
        mdc("correlation-id") { call ->
            call.request.headers.get("x-correlation-id") ?: UUID.randomUUID().toString()
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            validate { credentials ->
                if (credentials.name == "foo" && credentials.password == "bar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    install(StatusPages) {
        exception<SerializationException> { cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.message}")
            throw cause
        }
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.message}")
            throw cause
        }
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${cause.message}")
            throw cause
        }
        status(HttpStatusCode.NotFound) {
            call.respond(
                TextContent(
                    "${it.value} ${it.description}",
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    it
                )
            )
        }
        status(HttpStatusCode.Unauthorized) {
            call.respond(
                TextContent(
                    "${it.value} ${it.description}",
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    it
                )
            )
        }
    }

    registerPokemonRoutes()
}
