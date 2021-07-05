package dev.danielwright.main

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.BadRequestException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.koin.ktor.ext.inject

fun Application.registerPokemonRoutes() {
    routing {
        getPokemonRoutes()
    }
}

fun Route.getPokemonRoutes() {

    val pokemonService: PokemonService by inject()
    val configService: ConfigService by inject()
    val config: Config = configService.getConfig()
    val validator = Validation.buildDefaultValidatorFactory().validator

    get("/hello") {
        call.respondText("Hello, World!")
    }
    get("/pokemon") {
        call.respond(pokemonService.getRandomPokemon())
    }
    post("/pokemon") {
        val pokemonByIdRequest = call.receive<GetPokemonByIdRequest>()
        pokemonByIdRequest.validate(validator)
        call.respond(pokemonService.getPokemonById(pokemonByIdRequest.id))
    }
    get("/pokefail") {
        throw Exception("Pokefail exception")
    }
    get("/config") {
        call.respond("Default pokemon is ${config.defaultPokemon}")
    }
    authenticate("auth-basic") {
        get("/protectedpokemon") {
            call.respond("PROTECTED!")
        }
    }
}

@Throws(BadRequestException::class)
fun <T : Any> T.validate(validator: Validator) {
    validator.validate(this)
        .takeIf { it.isNotEmpty() }
        ?.let {
            val first = it.first()
            throw BadRequestException("Bad request: ${first.propertyPath} ${first.message}")
        }
}
