package dev.danielwright.main

import org.koin.dsl.module
import org.koin.dsl.single

val pokemonAppModule = module(createdAtStart = true) {
    single<PokemonAPIClient>()
    single<PokemonService>()
}
