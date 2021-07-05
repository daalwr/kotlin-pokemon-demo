package dev.danielwright.main

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import kotlinx.serialization.SerializationException
import org.koin.ktor.ext.Koin
import java.util.*

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
    install(CORS)
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