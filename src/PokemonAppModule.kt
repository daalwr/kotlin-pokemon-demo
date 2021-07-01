package dev.danielwright

import org.koin.dsl.module
import org.koin.dsl.single

val helloAppModule = module(createdAtStart = true) {
    single<PokemonAPIClient>()
    single<PokemonService>()
}
